(ns perc.alpha.core
  (:require [clojure.string :as string]
            #?(:cljs [cljs.reader :refer [read-string]])
            [clojure.walk :as w]
            [dispacio.alpha.core :refer [defp]]))

(defn get-keyword [{:keys [kns kn]}]
  (if kn
    (if (= "" kns)
      (keyword (-> ::_ namespace) kn)
      (keyword kns kn))
    (keyword kns)))

(defn vararg-index [sym expr]
  (let [nums (->> expr
               (tree-seq coll? seq)
               (filter #(string/starts-with? (str %) (str sym)))
               (map #(re-find #"\d" (str %)))
               (filter some?)
               (map read-string))]
    (if (not (empty? nums))
      (apply max nums)
      0)))

(defn get-val [{:keys [sym expr result index vararg?] :as state}]
  (if-not (or index vararg?)
    `(first ~result)
    (if vararg?
      (let [vargi (vararg-index sym expr)]
        (if (not index)
          `(drop ~vargi ~result)
          `(nth (drop ~vargi ~result) ~(dec index))))
      `(nth ~result ~(dec index)))))

(defn element-starts-with-symbol? [sym el]
  (and (= (str sym) (->> el str (take-while #{(first (str sym))}) (apply str)))
    (= (str sym) (->> el str (take (count sym)) (apply str)))))

(defn mk-sym-state [{:keys [sym el] :as state}]
  (let [[param kns kn] (string/split (str el) #":")
        index (->> param (drop (count sym)) (apply str))
        index (if (= index "") "nil" index)
        vararg? (string/starts-with? index "&")
        index (if-not vararg? (read-string index)
                (when (re-find #".\d" index)
                  (read-string (apply str (rest index)))))
        result (symbol (str "perclocal" sym))]
    (assoc state :index index :kns kns :kn kn :result result :vararg? vararg?)))

(defn mk-sym [perc-state]
  (let [{:keys [kns kn vararg? index] :as state} (mk-sym-state perc-state)
        v (get-val state)]
    (if-not (or kns kn)
      `~v
      (if (and (not index) vararg?)
        `(get (apply hash-map ~v) ~(get-keyword state))
        `(get ~v ~(get-keyword state))))))

(defn handle [{:keys [sym] :as state} el]
  (if (= el (symbol (str "perclocal" sym)))
    (throw (Exception. (str "No nesting for reader tag #%/" sym)))
    (if (element-starts-with-symbol? sym el)
      (mk-sym (assoc state :el el))
      el)))

(defn mk-perc [sym expr]
  `(fn [& ~(symbol (str "perclocal" sym))]
     ~(w/postwalk (partial handle {:expr expr :sym (str sym)}) expr)))

(defp perc :poly/default ; #(-> %& first str (= "%*"))
  [sym expr]
  (mk-perc sym expr))

(defn % [expr]
  (perc '% expr))

(defn %% [expr]
  (perc '%% expr))

(defn %%% [expr]
  (perc '%%% expr))

(defn $ [expr]
  (perc '$ expr))

(defn $$ [expr]
  (perc '$$ expr))

(defn $$$ [expr]
  (perc '$$$ expr))

(defn ? [expr]
  (perc '? expr))

(defn ?? [expr]
  (perc '?? expr))

(defn ??? [expr]
  (perc '??? expr))
