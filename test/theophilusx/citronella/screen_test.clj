(ns theophilusx.citronella.screen-test
  (:require [theophilusx.citronella.screen :as sut]
            [clojure.test :refer [deftest testing is]]
            [clojure.string :as string])
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
      (is (= \space (:char c2))))
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

(deftest put-string-tests
  (sut/start scrn)
  (testing (str "basic put string: " (:type @scrn))
    (let [test-str "Put string test"]
      (sut/put-string scrn test-str 5 5)
      (let [cs1 (mapv #(sut/get-back-char scrn % 5)
                      (range 5 (+ 5 (count test-str))))
            s1 (string/join "" (mapv :char cs1))]
        (is (= s1 test-str))
        (is (every? #(= "DEFAULT" (:fg %)) cs1))
        (is (every? #(= "DEFAULT" (:bg %)) cs1))
        (is (every? #(= [] (:modifiers %)) cs1)))
      (let [cs2 (mapv #(sut/get-front-char scrn % 5)
                      (range 5 (+ 5 (count test-str))))
            s2 (string/join (map :char cs2))]
        (is (not= s2 test-str)))
      (sut/refresh scrn)
      (let [cs3 (mapv #(sut/get-front-char scrn % 5)
                      (range 5 (+ 5 (count test-str))))
            s3 (string/join "" (map :char cs3))]
        (is (= s3 test-str))
        (is (every? #(= "DEFAULT" (:fg %)) cs3))
        (is (every? #(= "DEFAULT" (:bg %)) cs3))
        (is (every? #(= [] (:modifiers %)) cs3)))))
  (testing (str "put string with modifiers: " (:type @scrn))
    (let [test-str "test string with modifiers"]
      (sut/put-string scrn test-str 5 6 [:bold])
      (let [cs4 (mapv #(sut/get-back-char scrn % 6)
                      (range 5 (+ 5 (count test-str))))
            s4 (string/join (map :char cs4))]
        (is (= s4 test-str))
        (is (every? #(= "DEFAULT" (:fg %)) cs4))
        (is (every? #(= "DEFAULT" (:bg %)) cs4))
        (is (every? #(= [:bold] (:modifiers %)) cs4)))
      (let [cs5 (mapv #(sut/get-front-char scrn % 6)
                      (range 5 (+ 5 (count test-str))))
            s5 (string/join (map :char cs5))]
        (is (not= s5 test-str)))
      (sut/refresh scrn)
      (let [cs6 (mapv #(sut/get-front-char scrn % 6)
                      (range 5 (+ 5 (count test-str))))
            s6 (string/join (map :char cs6))]
        (is (= s6 test-str))
        (is (every? #(= "DEFAULT" (:fg %)) cs6))
        (is (every? #(= "DEFAULT" (:bg %)) cs6))
        (is (every? #(= [:bold] (:modifiers %)) cs6)))))
  (sut/stop scrn))

(deftest draw-tests
  (sut/start scrn)
  (testing (str "draw line test: " (:type @scrn))
    (sut/draw-line scrn 5 5 10 5 \-)
    (let [ls (map #(sut/get-back-char scrn % 5) (range 5 11))]
      (is (every? #(= \- (:char %)) ls)))
    (sut/refresh scrn)
    (let [ls2 (map #(sut/get-front-char scrn % 5) (range 5 11))]
      (is (every? #(= \- (:char %)) ls2))))
  (testing (str "draw rectangle test: " (:type @scrn))
    (sut/draw-rectangle scrn 5 6 5 3 \+)
    (let [t (map #(sut/get-back-char scrn % 6) (range 5 10))
          b (map #(sut/get-back-char scrn % 8) (range 5 10))
          l (map #(sut/get-back-char scrn 5 %) (range 6 9))
          r (map #(sut/get-back-char scrn 9 %) (range 6 9))]
      (is (every? #(= \+ (:char %)) t))
      (is (every? #(= \+ (:char %)) b))
      (is (every? #(= \+ (:char %)) l))
      (is (every? #(= \+ (:char %)) r)))
    (sut/refresh scrn)
    (let [t2 (map #(sut/get-front-char scrn % 6) (range 5 10))
          b2 (map #(sut/get-front-char scrn % 8) (range 5 10))
          l2 (map #(sut/get-front-char scrn 5 %) (range 6 9))
          r2 (map #(sut/get-front-char scrn 9 %) (range 6 9))]
      (is (every? #(= \+ (:char %)) t2))
      (is (every? #(= \+ (:char %)) b2))
      (is (every? #(= \+ (:char %)) l2))
      (is (every? #(= \+ (:char %)) r2))))
  (sut/stop scrn))

(deftest fill-tests
  (sut/start scrn)
  (testing (str "Fill tests: " (:type @scrn))
    (sut/fill scrn \a)
    (let [cs (map #(sut/get-back-char scrn (first %) (second %))
                  (vec (for [c (range 0 80)
                             r (range 0 24)]
                         [c r])))]
      (is (every? #(= \a (:char %)) cs)))
    (sut/refresh scrn)
    (let [cs2 (map #(sut/get-back-char scrn (first %) (second %))
                  (vec (for [c (range 0 80)
                             r (range 0 24)]
                         [c r])))]
      (is (every? #(= \a (:char %)) cs2))))
  (testing (str "Fill region tests: " (:type @scrn))
    (sut/fill-rectangle scrn 5 5 10 10 \b)
    (let [cs (map #(sut/get-back-char scrn (first %) (second %))
                  (vec (for [c (range 5 15)
                             r (range 5 15)]
                         [c r])))]
      (is (every? #(= \b (:char %)) cs)))
    (sut/refresh scrn)
    (let [cs2 (map #(sut/get-front-char scrn (first %) (second %))
                  (vec (for [c (range 5 15)
                             r (range 5 15)]
                         [c r])))]
      (is (every? #(= \b (:char %)) cs2))))
  (sut/stop scrn))

(deftest test-tg-colors
  (sut/start scrn)
  (testing (str "Text graphics foreground test: " (:type @scrn))
    (sut/set-tg-fg scrn "red")
    (sut/fill scrn \space)
    (let [cs (map #(sut/get-back-char scrn (first %) (second %))
                  (vec (for [c (range 0 80)
                             r (range 0 24)]
                         [c r])))]
      (is (every? #(= "RED" (:fg %)) cs)))
    (sut/refresh scrn)
    (let [cs2 (map #(sut/get-front-char scrn (first %) (second %))
                  (vec (for [c (range 0 80)
                             r (range 0 24)]
                         [c r])))]
      (is (every? #(= "RED" (:fg %)) cs2))))
  (testing (str "Text graphics background test: " (:type @scrn))
    (sut/set-tg-bg scrn "yellow")
    (sut/fill scrn \space)
    (let [cs (map #(sut/get-back-char scrn (first %) (second %))
                  (vec (for [c (range 0 80)
                             r (range 0 24)]
                         [c r])))]
      (is (every? #(= "YELLOW" (:bg %)) cs)))
    (sut/refresh scrn)
    (let [cs2 (map #(sut/get-front-char scrn (first %) (second %))
                   (vec (for [c (range 0 80)
                              r (range 0 24)]
                          [c r])))]
      (is (every? #(= "YELLOW" (:bg %)) cs2))))
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
    (put-string-tests)
    (draw-tests)
    (fill-tests)
    (test-tg-colors)
    (sut/close scrn)))

(defn test-ns-hook []
  (get-screen-tests)
  (screen-function-tests))
