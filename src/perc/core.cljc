(ns perc.core
  (:require [clojure.string :as string]
            #?(:cljs [cljs.reader :refer [read-string]])
            [clojure.walk :as w]))

(defn element-starts-with-symbol? [sym el]
  (and (= (str sym)
          (->> el str (take-while #{(first (str sym))}) (apply str)))
       (= (str sym)
          (->> el str (take (count sym)) (apply str)))))

(defn extract-& [s]
  (if (string/starts-with? s "&")
    [(apply str (rest s))
     "&"]
    [s nil]))

(def nums
  (->> "0123456789"
       (into #{})))

(defn extract-numbers [s]
  (let [res (if (-> s first nums)
              [(->> s (drop-while nums) (apply str))
               (->> s (take-while nums) (apply str))]
              [s nil])]
    res))

(defn extract-star [s]
  (if (string/starts-with? s "*")
    [(apply str (rest s))
     "*"]
    [s nil]))

(defn smells-like-keyword [s]
  (some-> s (string/starts-with? ":")))

(defn read-key [s]
  (let [s1 (if-not (and (re-find #"/" s)
                        (-> s first str (= "*")))
             (str ":" s)
             (if (->> s (take 2) (apply str) (= "*/"))
               (->> s (drop 2) (apply str "::"))
               (->> s rest (apply str "::"))))]
    (if (and (re-find #"/" s1) (string/starts-with? s1 "::"))
      (let [[n k] (string/split s1 #"/")
            n (->> n (drop 2) (apply str))
            k2 (->> k (drop 2) (apply str))
            n2 (str (get (ns-aliases *ns*) (symbol n)))]
        (if k
          (keyword n2 k)
          (keyword n)))
      (if (string/starts-with? s1 "::")
        (keyword (str *ns*) (->> s1 (drop 2) (apply str)))
        (keyword (->> s1 rest (apply str)))))))

(defn read-keys [s]
  (let [ks (-> s (string/split #":") rest)]
    (when (seq ks)
      (if (= 1 (count ks))
        [(read-key (first ks))]
        (mapv read-key ks)))))

(defn explode [s]
  (let [[s vararg?] (extract-& s)
        [s index]   (extract-numbers s)
        index (some-> index read-string dec)
        new-keys (when (smells-like-keyword s)
                   (read-keys s))
        new-sym (when-not (seq new-keys)
                  (when (and s (not (= s "")))
                    (symbol s)))
        thread (into []
                     (filter #(not (nil? %))
                             (if new-sym
                               [index new-sym]
                               (into [index] new-keys))))]
    {:vararg? vararg?
     :index index
     :new-keys new-keys
     :thread thread}))

(defn one-token [{:keys [root-local sym el] :as state}]
  (let [result (symbol (str "perclocal" sym))
        ex (if el (explode el) {:thread [root-local]})]
    (select-keys ex [:index :vararg? :thread :new-keys])))

(defn mk-sym-state [{:keys [sym el] :as state}]
  (let [drop-el (dec (count sym))
        el (->> el str (drop drop-el) (apply str))
        tokens (-> el
                   str
                   (string/split #"%")
                   rest)
        nt (->> tokens
                (mapv (fn [token]
                        (if (string/starts-with? token "*:")
                          (apply str ":" (rest token))
                          token))))
        one-tkn? (= 1 (count tokens))
        token-thread (->> nt
                          (mapv #(one-token (assoc state :el % :token %)))
                          (mapv :thread)
                          (apply concat)
                          vec)
        first-token (assoc
                     (one-token (assoc state
                                       :el (first nt)
                                       :token (first nt)))
                     :token-thread token-thread)]
    first-token))

(defn vararg-index [sym expr]
  (let [nums (->> expr
                  (tree-seq coll? seq)
                  (filter #(string/starts-with? (str %) (str sym)))
                  (map #(re-find #"^%\d" (str %)))
                  (filter some?)
                  (map rest)
                  (map #(apply str %))
                  (map read-string))]
    (if (not (empty? nums))
      (apply max nums)
      0)))

(defn mk-sym [{:as perc-state :keys [root-local threaded? expr sym]}]
  (let [{:keys [vararg?
                index
                token-thread] :as state}
        (mk-sym-state perc-state)
        vindex (vararg-index sym expr)
        obj (if threaded?
              `(first (vec ~root-local))
              (if-not vararg?
                `(vec ~root-local)
                `(apply hash-map (drop ~vindex ~root-local))))
        thread (if (or threaded? index vararg?)
                 token-thread
                 (into [0]
                       token-thread))
        get-thread (->> thread
                        (mapv (fn [tkn]
                                (if (int? tkn)
                                  (list 'nth tkn)
                                  tkn))))
        res `(-> ~obj ~@get-thread)]
    res))

(defn handle [{:keys [sym threaded? root-local] :as state} el]
  (if (= el root-local)
    (throw (Exception. (str "No nesting for reader tag #" sym)))
    (if (element-starts-with-symbol? sym el)
      (mk-sym (assoc state :el el))
      el)))

(defn mk-perc [sym threaded? expr]
  (let [root-local (symbol (str "perclocal" sym))
        res `(fn [& ~root-local]
               ~(w/postwalk
                 (partial handle
                          {:threaded? threaded?
                           :root-local root-local
                           :expr expr
                           :sym (str sym)})
                 expr))]
    res))

(defn perc
  [sym threaded? expr]
  (mk-perc sym threaded? expr))

(defn % [expr & [threaded?]]
  (let [res (->> expr
                 (perc '% threaded?))]
    res))

(defn %1 [expr]
  (% expr true))

(defn %% [expr & [threaded?]]
  (->> expr
       (perc '%% threaded?)))

(defn %%1 [expr]
  (%% expr true))

(defn %%% [expr & [threaded?]]
  (->> expr
       (perc '%%% threaded?)))

(defn %%%1 [expr]
  (%%% expr true))

(defn %> [expr]
  (let [perc-expr (% expr true)]
    `(~perc-expr)))

(defn %%> [expr]
  (let [perc-expr (%% expr true)]
    `(~perc-expr)))

(defn %%%> [expr]
  (let [perc-expr (%%% expr true)]
    `(~perc-expr)))

(defn -main [& args]
  (println :hi args))
