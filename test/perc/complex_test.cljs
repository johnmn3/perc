(ns perc.complex-test
  (:require [cljs.test :as t]))

(t/deftest nesting%
  (t/testing "nesting %%% in a %% in a %"
    (t/is (= 1 (#%/%(inc (#%/%%(dec (#%/%%%(inc %))))) 0)))
    (t/is (= 1 (#%/%(inc (#%/%%(dec (#%/%%%(inc %))))) 0 nil)))

    (t/is (= 1 (#%/%(inc (#%/%%(dec (#%/%%%(inc %1))))) 0)))
    (t/is (= 1 (#%/%(inc (#%/%%(dec (#%/%%%(inc %1))))) 0 nil)))
    (t/is (= 1 (#%/%(inc (#%/%%(dec (#%/%%%(inc %2))))) nil 0)))
    (t/is (= 1 (#%/%(inc (#%/%%(dec (#%/%%%(inc %3))))) nil nil 0)))
    (t/is (= 1 (#%/%(inc (#%/%%(dec (#%/%%%(inc %3))))) nil nil 0 nil)))

    (t/is (= 1 (#%/%(inc (#%/%%(dec (#%/%%%(inc %10))))) nil nil nil nil nil nil nil nil nil 0)))))

(t/deftest nesting%?$
  (t/testing "nesting % in a ? in a $"
    (t/is (= 1 (#%/%(inc (#%/?(dec (#%/$(inc %))))) 0)))
    (t/is (= 1 (#%/%(inc (#%/?(dec (#%/$(inc %))))) 0 nil)))

    (t/is (= 1 (#%/%(inc (#%/?(dec (#%/$(inc %1))))) 0)))
    (t/is (= 1 (#%/%(inc (#%/?(dec (#%/$(inc %1))))) 0 nil)))
    (t/is (= 1 (#%/%(inc (#%/?(dec (#%/$(inc %2))))) nil 0)))
    (t/is (= 1 (#%/%(inc (#%/?(dec (#%/$(inc %3))))) nil nil 0)))
    (t/is (= 1 (#%/%(inc (#%/?(dec (#%/$(inc %3))))) nil nil 0 nil)))

    (t/is (= 1 (#%/%(inc (#%/?(dec (#%/$(inc %10))))) nil nil nil nil nil nil nil nil nil 0)))))

(t/deftest nesting-updates
  (t/testing "complex nesting % and %%"
    (let [state (atom {:fs {:home #{"/pics"}}})
          path  (atom :home)
          conf {:current-path path
                :state state}]
      (t/is (= (do
                 (-> conf
                   (#%/%(swap! %:state
                         #%/%%(update-in %% [:fs @%:current-path] empty))))
                 @state)
              {:fs {:home #{}}}))))

  (t/testing "complex nesting % and $"
    (let [state (atom {:fs {:home #{"/pics"}}})
          path  (atom :home)
          conf {:current-path path
                :state state}]
      (t/is (= (do
                 (-> conf
                   (#%/%(swap! %:state
                         #%/$(update-in $ [:fs @%:current-path] empty))))
                 @state)
              {:fs {:home #{}}})))))
