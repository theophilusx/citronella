(ns theophilusx.citronella.text-graphics-test
  (:require [theophilusx.citronella.text-graphics :as sut]
            [clojure.test :refer [deftest testing is]]
            [theophilusx.citronella.terminal :as terminal]))

(def textg (atom nil))

(deftest put-string-tests
  (testing "put-string 1"
    (sut/put-string @textg "This is a test" 5 5)
    (is (= \T (:char (sut/get-char @textg 5 5))))
    (is (= \i (:char (sut/get-char @textg 10 5))))
    (is (= \t (:char (sut/get-char @textg 15 5)))))
  (testing "put-string 2"
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

(defn test-ns-hook []
  (let [term (terminal/get-terminal {:type :gui})]
    (reset! textg (:text-graphics @term))
    (put-string-tests)
    (terminal/close term)))
