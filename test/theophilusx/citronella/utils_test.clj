(ns theophilusx.citronella.utils-test
  (:require [theophilusx.citronella.utils :as sut]
            [clojure.test :refer [deftest testing is]]))

(deftest make-color-tests
  (testing "make ansi color"
    (let [c (sut/make-color "green")]
      (is (= (.toString c) "GREEN"))
      (is (= (.toString (sut/make-color c)) "GREEN"))))
  (testing "make index color"
    (let [c (sut/make-color "#17")]
      (is (= (.toString c) "{IndexedColor:17}"))
      (is (= (.toString (sut/make-color c)) "{IndexedColor:17}"))))
  (testing "make rgb color"
    (let [c (sut/make-color "#4c5b3d")]
      (is (= (.toString c) "{RGB:76,91,61}"))
      (is (= (.toString c) "{RGB:76,91,61}")))))

(deftest make-character-tests
  (testing "Basic make character test"
    (let [c (sut/make-character \a)]
      (is (= (sut/get-char c) \a))
      (is (= (sut/get-fg c) "DEFAULT"))
      (is (= (sut/get-bg c) "DEFAULT"))
      (is (= (sut/sgr-modifiers c) []))))
  (testing "Make character with color"
    (let [c (sut/make-character \a "green" "blue")]
      (is (= (sut/get-char c) \a))
      (is (= (sut/get-fg c) "GREEN"))
      (is (= (sut/get-bg c) "BLUE"))
      (is (= (sut/sgr-modifiers c) []))))
  (testing "Make character with modifiers"
    (let [c (sut/make-character \a "default" "default" [:bold :underline])]
      (is (= (sut/get-char c) \a))
      (is (= (sut/get-fg c) "DEFAULT"))
      (is (= (sut/get-bg c) "DEFAULT"))
      (is (= (sut/sgr-modifiers c) [:bold :underline])))))
