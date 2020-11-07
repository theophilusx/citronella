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

(deftest get-char-tests
  (sut/start scrn)
  (testing "get back char test"
    (let [c (sut/get-back-char scrn 10 10)]
      (is (map? c))
      (is (= (keys c) '(:char :fg :bg :modifiers)))
      (is (= \space (:char c)))
      (is (= "DEFAULT" (:fg c)))
      (is (= "DEFAULT" (:bg c)))
      (is (= [] (:modifiers c))))
    (let [c (sut/get-front-char scrn 10 10)]
      (is (map? c))
      (is (= (keys c) '(:char :fg :bg :modifiers)))
      (is (= \space (:char c)))
      (is (= "DEFAULT" (:fg c)))
      (is (= "DEFAULT" (:bg c)))
      (is (= [] (:modifiers c)))))
  (sut/stop scrn))

(deftest put-char-tests
  (sut/start scrn)
  (testing "basic put char test"
    (sut/put-char scrn 10 10 \t)
    (let [c1 (sut/get-back-char scrn 10 10)]
      (is (= \t (:char c1)))
      (is (= "DEFAULT" (:fg c1)))
      (is (= "DEFAULT" (:bg c1)))
      (is (= [] (:modifiers c1))))
    (let [c2 (sut/get-front-char scrn 10 10)]
      (is (= \space (:char c2)))
      (is (= "DEFAULT" (:fg c2)))
      (is (= "DEFAULT" (:bg c2)))
      (is (= [] (:modifiers c2))))
    (sut/refresh scrn)
    (let [c3 (sut/get-front-char scrn 10 10)]
      (is (= \t (:char c3)))))
  (testing "put char with color"
    (sut/put-char scrn 10 10 \a "blue" "green")
    (let [c4 (sut/get-back-char scrn 10 10)]
      (is (= \a (:char c4)))
      (is (= "BLUE" (:fg c4)))
      (is (= "GREEN" (:bg c4)))
      (is (= [] (:modifiers c4))))
    (let [c5 (sut/get-front-char scrn 10 10)]
      (is (= \t (:char c5))))
    (sut/refresh scrn)
    (let [c6 (sut/get-front-char scrn 10 10)]
      (is (= \a (:char c6)))
      (is (= "BLUE" (:fg c6)))
      (is (= "GREEN" (:bg c6)))
      (is (= [] (:modifiers c6)))))
  (testing "put char with color and modifiers"
    (sut/put-char scrn 10 10 \b "white" "yellow" [:bold])
    (sut/refresh scrn)
    (let [c7 (sut/get-back-char scrn 10 10)
          c8 (sut/get-front-char scrn 10 10)]
      (is (= \b (:char c7)))
      (is (= "WHITE" (:fg c7)))
      (is (= "YELLOW" (:bg c7)))
      (is (= [:bold] (:modifiers c7)))
      (is (= \b (:char c8)))
      (is (= "WHITE" (:fg c8)))
      (is (= "YELLOW" (:bg c8)))
      (is (= [:bold] (:modifiers c8)))))
  (sut/stop scrn))

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
    (get-char-tests)
    (put-char-tests)
    (sut/close scrn)))

(defn test-ns-hook []
  (get-screen-tests)
  (screen-function-tests))
