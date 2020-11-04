(ns theophilusx.citronella.text-graphics-test
  (:require [theophilusx.citronella.text-graphics :as sut]
            [clojure.test :refer [deftest testing is]]
            [theophilusx.citronella.terminal :as terminal]
            [clojure.string :as string]))

(def dest (atom nil))
(def textg (atom nil))
(def tg-source (atom nil))

(deftest put-string-tests
  (testing (str "put-string 1 - " @tg-source)
    (sut/put-string @textg "This is a test" 5 5)
    (is (= \T (:char (sut/get-char @textg 5 5))))
    (is (= \i (:char (sut/get-char @textg 10 5))))
    (is (= \t (:char (sut/get-char @textg 15 5)))))
  (testing (str "put-string 2 - " @tg-source)
    (sut/put-string @textg "This is a bold test" 5 6 [:bold])
    (let [c-data (sut/get-char @textg 5 6)]
      (is (= \T (:char c-data)))
      (is (= :bold (first (:modifiers c-data)))))
    (let [c-data (sut/get-char @textg 10 6)]
      (is (= \i (:char c-data)))
      (is (= :bold (first (:modifiers c-data)))))
    (let [c-data (sut/get-char @textg 15 6)]
      (is (= \b (:char c-data)))
      (is (= :bold (first (:modifiers c-data)))))))

(deftest draw-tests
  (testing (str "draw a line test - " @tg-source)
    (sut/draw-line @textg 5 7 10 7 \-)
    (is (= (string/join "" (map #(:char (sut/get-char @textg % 7))
                                [5 6 7 8 9 10]))
           "------")))
  (testing (str "draw a rectangle test - " @tg-source)
    (sut/draw-rectangle @textg 5 8 5 3 \#)
    (is (= (:char (sut/get-char @textg 5 8)) \#))
    (is (= (:char (sut/get-char @textg 9 8)) \#))
    (is (= (:char (sut/get-char @textg 5 10)) \#))
    (is (= (:char (sut/get-char @textg 9 10)) \#))))

(deftest fill-tests
  (testing (str "fill text graphics - " @tg-source)
    (sut/fill @textg \0)
    (let [[cols rows] (:size @dest)]
      (is (= (:char (sut/get-char @textg 0 0)) \0))
      (is (= (:char (sut/get-char @textg 0 (dec rows))) \0))
      (is (= (:char (sut/get-char @textg (dec cols) 0)) \0))
      (is (= (:char (sut/get-char @textg (dec cols) (dec rows))) \0))))
  (testing (str "fill rectangle test - " @tg-source)
    (sut/fill-rectangle @textg 5 5 10 10 \1)
    (is (= (:char (sut/get-char @textg 5 5)) \1))
    (is (= (:char (sut/get-char @textg 9 5)) \1))
    (is (= (:char (sut/get-char @textg 5 9)) \1))
    (is (= (:char (sut/get-char @textg 9 9)) \1))))

(deftest set-colours-test
  (testing (str "set foreground colour - " @tg-source)
    (sut/set-fg @textg "green")
    (sut/fill @textg \space)
    (is (= (:fg (sut/get-char @textg 5 5)) "GREEN"))
    (sut/set-bg @textg "magenta")
    (sut/fill @textg \space)
    (is (= (:bg (sut/get-char @textg 5 5)) "MAGENTA"))))

(defn test-ns-hook []
  (let [term (terminal/get-terminal {:type :gui})]
    (reset! dest @term)
    (reset! textg (:text-graphics @term))
    (reset! tg-source "terminal gui")
    (put-string-tests)
    (draw-tests)
    (fill-tests)
    (set-colours-test)
    (terminal/close term)))
