(ns theophilusx.citronella.screen-test
  (:require [theophilusx.citronella.screen :as sut]
            [clojure.test :refer [deftest testing is]])
  (:import [com.googlecode.lanterna.screen TerminalScreen]))

(def scrn (atom nil))

(deftest get-screen-tests
  (testing "Get screen no args"
    (let [s (sut/get-screen)]
      (is (map? @s))
      (is (= (:type @s) :auto))
      (is (not (:started? @s)))
      (is (vector? (:cursor @s)) true)
      (is (vector? (:size @s)))
      (is (= (type (:obj @s)) TerminalScreen))
      (sut/close s)))
  (testing "Get screen with :text type"
    (let [s (sut/get-screen {:type :text})]
      (is (map? @s))
      (is (= (:type @s) :text))
      (is (not (:started? @s)))
      (is (vector? (:cursor @s)) true)
      (is (vector? (:size @s)))
      (is (= (type (:obj @s)) TerminalScreen))
      (sut/close s)))
  (testing "Get screen with :guitype"
    (let [s (sut/get-screen {:type :gui})]
      (is (map? @s))
      (is (= (:type @s) :gui))
      (is (not (:started? @s)))
      (is (vector? (:cursor @s)) true)
      (is (vector? (:size @s)))
      (is (= (type (:obj @s)) TerminalScreen))
      (sut/close s))))

(deftest start-stop-test
  (testing "start sets started? to true"
    (sut/start scrn)
    (is (:started? @scrn))
    (sut/stop scrn)
    (is (not (:started? @scrn)))))

(deftest cursor-tests
  (testing "cursor position test"
    (is (= [0 0] (sut/cursor-position scrn))))
  (testing "set cursor position test"
    (is (= [12 10] (sut/set-cursor scrn 12 10)))))

(deftest screen-function-tests
  (doseq [s [:text :gui]]
    (reset! scrn @(sut/get-screen {:type s}))
    (start-stop-test)
    (cursor-tests)
    (testing "get screen size"
      (is (= [80 24] (sut/size scrn))))
    (testing "clear screen"
      (sut/clear scrn)
      (is (:need-refresh? @scrn)))
    (testing "refresh screen"
      (sut/refresh scrn)
      (is (not (:need-refresh? @scrn))))
    (sut/close scrn)))

(defn test-ns-hook []
  (get-screen-tests)
  (screen-function-tests))
