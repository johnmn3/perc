(ns perc.simple-test
  (:require [cljs.test :as t]))




(t/deftest perc-normal
  (t/testing "old school %"
    (t/is (= 1 (#%/%(inc %) 0))))
  (t/testing "permissive arity"
    (t/is (= 1 (#%/%(inc %) 0 nil))))
  (t/testing "by arity index"
    (t/is (= 1 (#%/%(inc %1) 0)))
    (t/is (= 1 (#%/%(inc %1) 0 nil)))
    (t/is (= 1 (#%/%(inc %2) nil 0)))
    (t/is (= 1 (#%/%(inc %3) nil nil 0)))
    (t/is (= 1 (#%/%(inc %3) nil nil 0 nil))))
  (t/testing "by large arity index"
    (t/is (= 1 (#%/%(inc %10) nil nil nil nil nil nil nil nil nil 0)))))

(t/deftest perc-keys
  (t/testing "by key %:x"
    (t/is (= 1 (#%/%(inc %:x) {:x 0})))
    (t/is (= 1 (#%/%(inc %:x) {:x 0} nil)))

    (t/is (= 1 (#%/%(inc %1:x) {:x 0})))
    (t/is (= 1 (#%/%(inc %1:x) {:x 0} nil)))
    (t/is (= 1 (#%/%(inc %2:x) nil {:x 0})))
    (t/is (= 1 (#%/%(inc %3:x) nil nil {:x 0})))
    (t/is (= 1 (#%/%(inc %3:x) nil nil {:x 0} nil)))

    (t/is (= 1 (#%/%(inc %10:x) nil nil nil nil nil nil nil nil nil {:x 0})))))

(t/deftest perc-namespaced-keys
  (t/testing "by namespaced key %::x"
    (t/is (= 1 (#%/%(inc %::x) {::x 0})))
    (t/is (= 1 (#%/%(inc %::x) {::x 0} nil)))

    (t/is (= 1 (#%/%(inc %1::x) {::x 0})))
    (t/is (= 1 (#%/%(inc %1::x) {::x 0} nil)))
    (t/is (= 1 (#%/%(inc %2:perc/x) nil {:perc/x 0})))
    (t/is (= 1 (#%/%(inc %3::t/x) nil nil {::t/x 0})))
    (t/is (= 1 (#%/%(inc %3:perc/x) nil nil {:perc/x 0} nil)))

    (t/is (= 1 (#%/%(inc %10::x) nil nil nil nil nil nil nil nil nil {::x 0})))))

(t/deftest double-perc
  (t/testing "double perc %%::x"
    (t/is (= 1 (#%/%%(inc %%) 0)))
    (t/is (= 1 (#%/%%(inc %%) 0 nil)))

    (t/is (= 1 (#%/%%(inc %%1) 0)))
    (t/is (= 1 (#%/%%(inc %%1) 0 nil)))
    (t/is (= 1 (#%/%%(inc %%2) nil 0)))
    (t/is (= 1 (#%/%%(inc %%3) nil nil 0)))
    (t/is (= 1 (#%/%%(inc %%3) nil nil 0 nil)))

    (t/is (= 1 (#%/%%(inc %%10) nil nil nil nil nil nil nil nil nil 0)))))

(t/deftest double-perc-keys
  (t/testing "double-perc map keys %:x"
    (t/is (= 1 (#%/%%(inc %%:x) {:x 0})))
    (t/is (= 1 (#%/%%(inc %%:x) {:x 0} nil)))

    (t/is (= 1 (#%/%%(inc %%1:x) {:x 0})))
    (t/is (= 1 (#%/%%(inc %%1:x) {:x 0} nil)))
    (t/is (= 1 (#%/%%(inc %%2:x) nil {:x 0})))
    (t/is (= 1 (#%/%%(inc %%3:x) nil nil {:x 0})))
    (t/is (= 1 (#%/%%(inc %%3:x) nil nil {:x 0} nil)))

    (t/is (= 1 (#%/%%(inc %%10:x) nil nil nil nil nil nil nil nil nil {:x 0})))))

(t/deftest double-perc-namespaced-keys
  (t/testing "double-perc namespaced keys %::x"
    (t/is (= 1 (#%/%%(inc %%::x) {::x 0})))
    (t/is (= 1 (#%/%%(inc %%::x) {::x 0} nil)))

    (t/is (= 1 (#%/%%(inc %%1::x) {::x 0})))
    (t/is (= 1 (#%/%%(inc %%1::x) {::x 0} nil)))
    (t/is (= 1 (#%/%%(inc %%2:perc/x) nil {:perc/x 0})))
    (t/is (= 1 (#%/%%(inc %%3::t/x) nil nil {::t/x 0})))
    (t/is (= 1 (#%/%%(inc %%3:perc/x) nil nil {:perc/x 0} nil)))

    (t/is (= 1 (#%/%%(inc %%10::x) nil nil nil nil nil nil nil nil nil {::x 0})))))


(t/deftest triple-perc
  (t/testing "triple perc %%%::x"
    (t/is (= 1 (#%/%%%(inc %%%) 0)))
    (t/is (= 1 (#%/%%%(inc %%%) 0 nil)))

    (t/is (= 1 (#%/%%%(inc %%%1) 0)))
    (t/is (= 1 (#%/%%%(inc %%%1) 0 nil)))
    (t/is (= 1 (#%/%%%(inc %%%2) nil 0)))
    (t/is (= 1 (#%/%%%(inc %%%3) nil nil 0)))
    (t/is (= 1 (#%/%%%(inc %%%3) nil nil 0 nil)))

    (t/is (= 1 (#%/%%%(inc %%%10) nil nil nil nil nil nil nil nil nil 0)))))

(t/deftest triple-perc-keys
  (t/testing "triple-perc map keys %%%:x"
    (t/is (= 1 (#%/%%%(inc %%%:x) {:x 0})))
    (t/is (= 1 (#%/%%%(inc %%%:x) {:x 0} nil)))

    (t/is (= 1 (#%/%%%(inc %%%1:x) {:x 0})))
    (t/is (= 1 (#%/%%%(inc %%%1:x) {:x 0} nil)))
    (t/is (= 1 (#%/%%%(inc %%%2:x) nil {:x 0})))
    (t/is (= 1 (#%/%%%(inc %%%3:x) nil nil {:x 0})))
    (t/is (= 1 (#%/%%%(inc %%%3:x) nil nil {:x 0} nil)))

    (t/is (= 1 (#%/%%%(inc %%%10:x) nil nil nil nil nil nil nil nil nil {:x 0})))))

(t/deftest triple-perc-namespaced-keys
  (t/testing "triple-perc namespaced keys %%%::x"
    (t/is (= 1 (#%/%%%(inc %%%::x) {::x 0})))
    (t/is (= 1 (#%/%%%(inc %%%::x) {::x 0} nil)))

    (t/is (= 1 (#%/%%%(inc %%%1::x) {::x 0})))
    (t/is (= 1 (#%/%%%(inc %%%1::x) {::x 0} nil)))
    (t/is (= 1 (#%/%%%(inc %%%2:perc/x) nil {:perc/x 0})))
    (t/is (= 1 (#%/%%%(inc %%%3::t/x) nil nil {::t/x 0})))
    (t/is (= 1 (#%/%%%(inc %%%3:perc/x) nil nil {:perc/x 0} nil)))

    (t/is (= 1 (#%/%%%(inc %%%10::x) nil nil nil nil nil nil nil nil nil {::x 0})))))









(t/deftest dollar-normal
  (t/testing "old school $"
    (t/is (= 1 (#%/$(inc $) 0)))
    (t/is (= 1 (#%/$(inc $) 0 nil)))

    (t/is (= 1 (#%/$(inc $1) 0)))
    (t/is (= 1 (#%/$(inc $1) 0 nil)))
    (t/is (= 1 (#%/$(inc $2) nil 0)))
    (t/is (= 1 (#%/$(inc $3) nil nil 0)))
    (t/is (= 1 (#%/$(inc $3) nil nil 0 nil)))

    (t/is (= 1 (#%/$(inc $10) nil nil nil nil nil nil nil nil nil 0)))))

(t/deftest dollar-keys
  (t/testing "map keys $:x"
    (t/is (= 1 (#%/$(inc $:x) {:x 0})))
    (t/is (= 1 (#%/$(inc $:x) {:x 0} nil)))

    (t/is (= 1 (#%/$(inc $1:x) {:x 0})))
    (t/is (= 1 (#%/$(inc $1:x) {:x 0} nil)))
    (t/is (= 1 (#%/$(inc $2:x) nil {:x 0})))
    (t/is (= 1 (#%/$(inc $3:x) nil nil {:x 0})))
    (t/is (= 1 (#%/$(inc $3:x) nil nil {:x 0} nil)))

    (t/is (= 1 (#%/$(inc $10:x) nil nil nil nil nil nil nil nil nil {:x 0})))))

(t/deftest dollar-namespaced-keys
  (t/testing "namespaced keys %::x"
    (t/is (= 1 (#%/$(inc $::x) {::x 0})))
    (t/is (= 1 (#%/$(inc $::x) {::x 0} nil)))

    (t/is (= 1 (#%/$(inc $1::x) {::x 0})))
    (t/is (= 1 (#%/$(inc $1::x) {::x 0} nil)))
    (t/is (= 1 (#%/$(inc $2:dollar/x) nil {:dollar/x 0})))
    (t/is (= 1 (#%/$(inc $3::t/x) nil nil {::t/x 0})))
    (t/is (= 1 (#%/$(inc $3:dollar/x) nil nil {:dollar/x 0} nil)))

    (t/is (= 1 (#%/$(inc $10::x) nil nil nil nil nil nil nil nil nil {::x 0})))))

(t/deftest double-dollar
  (t/testing "double dollar $$::x"
    (t/is (= 1 (#%/$$(inc $$) 0)))
    (t/is (= 1 (#%/$$(inc $$) 0 nil)))

    (t/is (= 1 (#%/$$(inc $$1) 0)))
    (t/is (= 1 (#%/$$(inc $$1) 0 nil)))
    (t/is (= 1 (#%/$$(inc $$2) nil 0)))
    (t/is (= 1 (#%/$$(inc $$3) nil nil 0)))
    (t/is (= 1 (#%/$$(inc $$3) nil nil 0 nil)))

    (t/is (= 1 (#%/$$(inc $$10) nil nil nil nil nil nil nil nil nil 0)))))

(t/deftest double-dollar-keys
  (t/testing "double-dollar map keys %:x"
    (t/is (= 1 (#%/$$(inc $$:x) {:x 0})))
    (t/is (= 1 (#%/$$(inc $$:x) {:x 0} nil)))

    (t/is (= 1 (#%/$$(inc $$1:x) {:x 0})))
    (t/is (= 1 (#%/$$(inc $$1:x) {:x 0} nil)))
    (t/is (= 1 (#%/$$(inc $$2:x) nil {:x 0})))
    (t/is (= 1 (#%/$$(inc $$3:x) nil nil {:x 0})))
    (t/is (= 1 (#%/$$(inc $$3:x) nil nil {:x 0} nil)))

    (t/is (= 1 (#%/$$(inc $$10:x) nil nil nil nil nil nil nil nil nil {:x 0})))))

(t/deftest double-dollar-namespaced-keys
  (t/testing "double-dollar namespaced keys %::x"
    (t/is (= 1 (#%/$$(inc $$::x) {::x 0})))
    (t/is (= 1 (#%/$$(inc $$::x) {::x 0} nil)))

    (t/is (= 1 (#%/$$(inc $$1::x) {::x 0})))
    (t/is (= 1 (#%/$$(inc $$1::x) {::x 0} nil)))
    (t/is (= 1 (#%/$$(inc $$2:dollar/x) nil {:dollar/x 0})))
    (t/is (= 1 (#%/$$(inc $$3::t/x) nil nil {::t/x 0})))
    (t/is (= 1 (#%/$$(inc $$3:dollar/x) nil nil {:dollar/x 0} nil)))

    (t/is (= 1 (#%/$$(inc $$10::x) nil nil nil nil nil nil nil nil nil {::x 0})))))


(t/deftest triple-dollar
  (t/testing "triple dollar $$$::x"
    (t/is (= 1 (#%/$$$(inc $$$) 0)))
    (t/is (= 1 (#%/$$$(inc $$$) 0 nil)))

    (t/is (= 1 (#%/$$$(inc $$$1) 0)))
    (t/is (= 1 (#%/$$$(inc $$$1) 0 nil)))
    (t/is (= 1 (#%/$$$(inc $$$2) nil 0)))
    (t/is (= 1 (#%/$$$(inc $$$3) nil nil 0)))
    (t/is (= 1 (#%/$$$(inc $$$3) nil nil 0 nil)))

    (t/is (= 1 (#%/$$$(inc $$$10) nil nil nil nil nil nil nil nil nil 0)))))

(t/deftest triple-dollar-keys
  (t/testing "triple-dollar map keys $$$:x"
    (t/is (= 1 (#%/$$$(inc $$$:x) {:x 0})))
    (t/is (= 1 (#%/$$$(inc $$$:x) {:x 0} nil)))

    (t/is (= 1 (#%/$$$(inc $$$1:x) {:x 0})))
    (t/is (= 1 (#%/$$$(inc $$$1:x) {:x 0} nil)))
    (t/is (= 1 (#%/$$$(inc $$$2:x) nil {:x 0})))
    (t/is (= 1 (#%/$$$(inc $$$3:x) nil nil {:x 0})))
    (t/is (= 1 (#%/$$$(inc $$$3:x) nil nil {:x 0} nil)))

    (t/is (= 1 (#%/$$$(inc $$$10:x) nil nil nil nil nil nil nil nil nil {:x 0})))))

(t/deftest triple-dollar-namespaced-keys
  (t/testing "triple-dollar namespaced keys $$$::x"
    (t/is (= 1 (#%/$$$(inc $$$::x) {::x 0})))
    (t/is (= 1 (#%/$$$(inc $$$::x) {::x 0} nil)))

    (t/is (= 1 (#%/$$$(inc $$$1::x) {::x 0})))
    (t/is (= 1 (#%/$$$(inc $$$1::x) {::x 0} nil)))
    (t/is (= 1 (#%/$$$(inc $$$2:dollar/x) nil {:dollar/x 0})))
    (t/is (= 1 (#%/$$$(inc $$$3::t/x) nil nil {::t/x 0})))
    (t/is (= 1 (#%/$$$(inc $$$3:dollar/x) nil nil {:dollar/x 0} nil)))

    (t/is (= 1 (#%/$$$(inc $$$10::x) nil nil nil nil nil nil nil nil nil {::x 0})))))















(t/deftest question-normal
  (t/testing "old school ?"
    (t/is (= 1 (#%/?(inc ?) 0)))
    (t/is (= 1 (#%/?(inc ?) 0 nil)))

    (t/is (= 1 (#%/?(inc ?1) 0)))
    (t/is (= 1 (#%/?(inc ?1) 0 nil)))
    (t/is (= 1 (#%/?(inc ?2) nil 0)))
    (t/is (= 1 (#%/?(inc ?3) nil nil 0)))
    (t/is (= 1 (#%/?(inc ?3) nil nil 0 nil)))

    (t/is (= 1 (#%/?(inc ?10) nil nil nil nil nil nil nil nil nil 0)))))

(t/deftest question-keys
  (t/testing "map keys ?:x"
    (t/is (= 1 (#%/?(inc ?:x) {:x 0})))
    (t/is (= 1 (#%/?(inc ?:x) {:x 0} nil)))

    (t/is (= 1 (#%/?(inc ?1:x) {:x 0})))
    (t/is (= 1 (#%/?(inc ?1:x) {:x 0} nil)))
    (t/is (= 1 (#%/?(inc ?2:x) nil {:x 0})))
    (t/is (= 1 (#%/?(inc ?3:x) nil nil {:x 0})))
    (t/is (= 1 (#%/?(inc ?3:x) nil nil {:x 0} nil)))

    (t/is (= 1 (#%/?(inc ?10:x) nil nil nil nil nil nil nil nil nil {:x 0})))))

(t/deftest question-namespaced-keys
  (t/testing "namespaced keys %::x"
    (t/is (= 1 (#%/?(inc ?::x) {::x 0})))
    (t/is (= 1 (#%/?(inc ?::x) {::x 0} nil)))

    (t/is (= 1 (#%/?(inc ?1::x) {::x 0})))
    (t/is (= 1 (#%/?(inc ?1::x) {::x 0} nil)))
    (t/is (= 1 (#%/?(inc ?2:question/x) nil {:question/x 0})))
    (t/is (= 1 (#%/?(inc ?3::t/x) nil nil {::t/x 0})))
    (t/is (= 1 (#%/?(inc ?3:question/x) nil nil {:question/x 0} nil)))

    (t/is (= 1 (#%/?(inc ?10::x) nil nil nil nil nil nil nil nil nil {::x 0})))))

(t/deftest double-question
  (t/testing "double question ??::x"
    (t/is (= 1 (#%/??(inc ??) 0)))
    (t/is (= 1 (#%/??(inc ??) 0 nil)))

    (t/is (= 1 (#%/??(inc ??1) 0)))
    (t/is (= 1 (#%/??(inc ??1) 0 nil)))
    (t/is (= 1 (#%/??(inc ??2) nil 0)))
    (t/is (= 1 (#%/??(inc ??3) nil nil 0)))
    (t/is (= 1 (#%/??(inc ??3) nil nil 0 nil)))

    (t/is (= 1 (#%/??(inc ??10) nil nil nil nil nil nil nil nil nil 0)))))

(t/deftest double-question-keys
  (t/testing "double-question map keys %:x"
    (t/is (= 1 (#%/??(inc ??:x) {:x 0})))
    (t/is (= 1 (#%/??(inc ??:x) {:x 0} nil)))

    (t/is (= 1 (#%/??(inc ??1:x) {:x 0})))
    (t/is (= 1 (#%/??(inc ??1:x) {:x 0} nil)))
    (t/is (= 1 (#%/??(inc ??2:x) nil {:x 0})))
    (t/is (= 1 (#%/??(inc ??3:x) nil nil {:x 0})))
    (t/is (= 1 (#%/??(inc ??3:x) nil nil {:x 0} nil)))

    (t/is (= 1 (#%/??(inc ??10:x) nil nil nil nil nil nil nil nil nil {:x 0})))))

(t/deftest double-question-namespaced-keys
  (t/testing "double-question namespaced keys %::x"
    (t/is (= 1 (#%/??(inc ??::x) {::x 0})))
    (t/is (= 1 (#%/??(inc ??::x) {::x 0} nil)))

    (t/is (= 1 (#%/??(inc ??1::x) {::x 0})))
    (t/is (= 1 (#%/??(inc ??1::x) {::x 0} nil)))
    (t/is (= 1 (#%/??(inc ??2:question/x) nil {:question/x 0})))
    (t/is (= 1 (#%/??(inc ??3::t/x) nil nil {::t/x 0})))
    (t/is (= 1 (#%/??(inc ??3:question/x) nil nil {:question/x 0} nil)))

    (t/is (= 1 (#%/??(inc ??10::x) nil nil nil nil nil nil nil nil nil {::x 0})))))


(t/deftest triple-question
  (t/testing "triple question ???::x"
    (t/is (= 1 (#%/???(inc ???) 0)))
    (t/is (= 1 (#%/???(inc ???) 0 nil)))

    (t/is (= 1 (#%/???(inc ???1) 0)))
    (t/is (= 1 (#%/???(inc ???1) 0 nil)))
    (t/is (= 1 (#%/???(inc ???2) nil 0)))
    (t/is (= 1 (#%/???(inc ???3) nil nil 0)))
    (t/is (= 1 (#%/???(inc ???3) nil nil 0 nil)))

    (t/is (= 1 (#%/???(inc ???10) nil nil nil nil nil nil nil nil nil 0)))))

(t/deftest triple-question-keys
  (t/testing "triple-question map keys ???:x"
    (t/is (= 1 (#%/???(inc ???:x) {:x 0})))
    (t/is (= 1 (#%/???(inc ???:x) {:x 0} nil)))

    (t/is (= 1 (#%/???(inc ???1:x) {:x 0})))
    (t/is (= 1 (#%/???(inc ???1:x) {:x 0} nil)))
    (t/is (= 1 (#%/???(inc ???2:x) nil {:x 0})))
    (t/is (= 1 (#%/???(inc ???3:x) nil nil {:x 0})))
    (t/is (= 1 (#%/???(inc ???3:x) nil nil {:x 0} nil)))

    (t/is (= 1 (#%/???(inc ???10:x) nil nil nil nil nil nil nil nil nil {:x 0})))))

(t/deftest triple-question-namespaced-keys
  (t/testing "triple-question namespaced keys ???::x"
    (t/is (= 1 (#%/???(inc ???::x) {::x 0})))
    (t/is (= 1 (#%/???(inc ???::x) {::x 0} nil)))

    (t/is (= 1 (#%/???(inc ???1::x) {::x 0})))
    (t/is (= 1 (#%/???(inc ???1::x) {::x 0} nil)))
    (t/is (= 1 (#%/???(inc ???2:question/x) nil {:question/x 0})))
    (t/is (= 1 (#%/???(inc ???3::t/x) nil nil {::t/x 0})))
    (t/is (= 1 (#%/???(inc ???3:question/x) nil nil {:question/x 0} nil)))

    (t/is (= 1 (#%/???(inc ???10::x) nil nil nil nil nil nil nil nil nil {::x 0})))))
