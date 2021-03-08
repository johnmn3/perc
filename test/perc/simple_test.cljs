(ns perc.simple-test
  (:require [cljs.test :as t]
            [clojure.string :as s]
            [perc.core :as perc]))

(t/deftest perc-normal
  (t/testing "old school %"
    (t/is (= 1 (#%(inc %) 0))))
  (t/testing "permissive arity"
    (t/is (= 1 (#%(inc %) 0 nil))))
  (t/testing "by arity index"
    (t/is (= 1 (#%(inc %1) 0)))
    (t/is (= 1 (#%(inc %1) 0 nil)))
    (t/is (= 1 (#%(inc %2) nil 0)))
    (t/is (= 1 (#%(inc %3) nil nil 0)))
    (t/is (= 1 (#%(inc %3) nil nil 0 nil))))
  (t/testing "by large arity index"
    (t/is (= 1 (#%(inc %10) nil nil nil nil nil nil nil nil nil 0)))))

(t/deftest perc-keys
  (t/testing "by key %:x"
    (t/is (= 1 (#%(inc %:x:y) {:x {:y 0}})))
    (t/is (= 1 (#%(inc %:x) {:x 0} nil)))

    (t/is (= 1 (#%(inc %1:x) {:x 0})))
    (t/is (= 1 (#%(inc %1:x) {:x 0} nil)))
    (t/is (= 1 (#%(inc %2:x) nil {:x 0})))
    (t/is (= 1 (#%(inc %3:x) nil nil {:x 0})))
    (t/is (= 1 (#%(inc %3:x) nil nil {:x 0} nil)))

    (t/is (= 1 (#%(inc %10:x) nil nil nil nil nil nil nil nil nil {:x 0})))))

(t/deftest perc-namespaced-keys
  (t/testing "by namespaced key %::x"
    (t/is (= 1 (#%(inc %:*/x) {::x 0})))
    (t/is (= 1 (#%(inc %:*/x) {::x 0} nil)))

    (t/is (= 1 (#%(inc %1:*/x) {::x 0})))
    (t/is (= 1 (#%(inc %1:*/x) {::x 0} nil)))
    (t/is (= 1 (#%(inc %2:perc/x) nil {:perc/x 0})))
                   ;;  %3:*/t/x
                   ;;  %1:*/s/y
    ;; (t/is (= 1 (#%(inc %3::t/x) nil nil {::t/x 0})))
    (t/is (= 1 (#%(inc %3:*s/x) nil nil {::s/x 0})))
    (t/is (= 1 (#%(inc %3:perc/x) nil nil {:perc/x 0} nil)))

    (t/is (= 1 (#%(inc %10:*/x) nil nil nil nil nil nil nil nil nil {::x 0})))))

(t/deftest double-perc
  (t/testing "double perc %%::x"
    (t/is (= 1 (#%%(inc %%) 0)))
    (t/is (= 2 (#%%(inc %%) 1 nil)))

    (t/is (= 3 (#%%(inc %%1) 2)))
    (t/is (= 4 (#%%(inc %%1) 3 nil)))
    (t/is (= 1 (#%%(inc %%2) nil 0)))
    (t/is (= 1 (#%%(inc %%3) nil nil 0)))
    (t/is (= 1 (#%%(inc %%3) nil nil 0 nil)))

    (t/is (= 1 (#%%(inc %%10) nil nil nil nil nil nil nil nil nil 0)))))

(t/deftest double-perc-keys
  (t/testing "double-perc map keys %:x"
    (t/is (= 1 (#%%(inc %%:x) {:x 0})))
    (t/is (= 1 (#%%(inc %%:x) {:x 0} nil)))

    (t/is (= 1 (#%%(inc %%1:x) {:x 0})))
    (t/is (= 1 (#%%(inc %%1:x) {:x 0} nil)))
    (t/is (= 1 (#%%(inc %%2:x) nil {:x 0})))
    (t/is (= 1 (#%%(inc %%3:x) nil nil {:x 0})))
    (t/is (= 1 (#%%(inc %%3:x) nil nil {:x 0} nil)))

    (t/is (= 1 (#%%(inc %%10:x) nil nil nil nil nil nil nil nil nil {:x 0})))))

(t/deftest double-perc-namespaced-keys
  (t/testing "double-perc namespaced keys %::x"
    (t/is (= 1 (#%%(inc %%:*/x) {::x 0})))
    (t/is (= 1 (#%%(inc %%:*/x) {::x 0} nil)))

    (t/is (= 1 (#%%(inc %%1:*/x) {::x 0})))
    (t/is (= 1 (#%%(inc %%1:*/x) {::x 0} nil)))
    (t/is (= 1 (#%%(inc %%2:perc/x) nil {:perc/x 0})))
    (t/is (= 1 (#%%(inc %%3:*t/x) nil nil {::t/x 0})))
    (t/is (= 1 (#%%(inc %%3:perc/x) nil nil {:perc/x 0} nil)))

    (t/is (= 1 (#%%(inc %%10:*/x) nil nil nil nil nil nil nil nil nil {::x 0})))))


(t/deftest triple-perc
  (t/testing "triple perc %%%::x"
    (t/is (= 1 (#%%%(inc %%%) 0)))
    (t/is (= 1 (#%%%(inc %%%) 0 nil)))

    (t/is (= 1 (#%%%(inc %%%1) 0)))
    (t/is (= 1 (#%%%(inc %%%1) 0 nil)))
    (t/is (= 1 (#%%%(inc %%%2) nil 0)))
    (t/is (= 1 (#%%%(inc %%%3) nil nil 0)))
    (t/is (= 1 (#%%%(inc %%%3) nil nil 0 nil)))

    (t/is (= 1 (#%%%(inc %%%10) nil nil nil nil nil nil nil nil nil 0)))))

(t/deftest triple-perc-keys
  (t/testing "triple-perc map keys %%%:x"
    (t/is (= 1 (#%%%(inc %%%:x) {:x 0})))
    (t/is (= 1 (#%%%(inc %%%:x) {:x 0} nil)))

    (t/is (= 1 (#%%%(inc %%%1:x) {:x 0})))
    (t/is (= 1 (#%%%(inc %%%1:x) {:x 0} nil)))
    (t/is (= 1 (#%%%(inc %%%2:x) nil {:x 0})))
    (t/is (= 1 (#%%%(inc %%%3:x) nil nil {:x 0})))
    (t/is (= 1 (#%%%(inc %%%3:x) nil nil {:x 0} nil)))

    (t/is (= 1 (#%%%(inc %%%10:x) nil nil nil nil nil nil nil nil nil {:x 0})))))

(t/deftest triple-perc-namespaced-keys
  (t/testing "triple-perc namespaced keys %%%::x"
    (t/is (= 1 (#%%%(inc %%%:*/x) {::x 0})))
    (t/is (= 1 (#%%%(inc %%%:*/x) {::x 0} nil)))

    (t/is (= 1 (#%%%(inc %%%1:*/x) {::x 0})))
    (t/is (= 1 (#%%%(inc %%%1:*/x) {::x 0} nil)))
    (t/is (= 1 (#%%%(inc %%%2:perc/x) nil {:perc/x 0})))
    (t/is (= 1 (#%%%(inc %%%3:*t/x) nil nil {::t/x 0})))
    (t/is (= 1 (#%%%(inc %%%3:perc/x) nil nil {:perc/x 0} nil)))

    (t/is (= 1 (#%%%(inc %%%10:*/x) nil nil nil nil nil nil nil nil nil {::x 0})))))

