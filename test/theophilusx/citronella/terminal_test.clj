(ns theophilusx.citronella.terminal-test
  (:require [theophilusx.citronella.terminal :as sut]
            [clojure.test :refer [deftest testing is]]
            [clojure.string :as string]))

(def term (atom nil))

(deftest get-terminal-tests
  (testing "get-terminal no args"
    (let [terminal (sut/get-terminal)]
      (is (= (map? @terminal) true))
      (is (= (:type @terminal) :auto))
      (is (= (:open @terminal) true))
      (is (= (:private @terminal) false))
      (is (vector? (:cursor @terminal)))
      (is (= (:cursor-visible @terminal) true))
      (is (= (:size @terminal) [80 24]))
      (sut/close terminal)))
  (testing "get-terminalinal :auto arg"
    (let [terminal (sut/get-terminal {:type :auto})]
      (is (= (map? @terminal) true))
      (is (= (:type @terminal) :auto))
      (is (= (:open @terminal) true))
      (is (= (:private @terminal) false))
      (is (vector? (:cursor @terminal)))
      (is (= (:cursor-visible @terminal) true))
      (is (= (:size @terminal) [80 24]))
      (sut/close terminal)))
  (testing "get-terminal :text arg"
    (let [terminal (sut/get-terminal {:type :text})]
      (is (= (map? @terminal) true))
      (is (= (:type @terminal) :text))
      (is (= (:open @terminal) true))
      (is (= (:private @terminal) false))
      (is (vector? (:cursor @terminal)))
      (is (= (:cursor-visible @terminal) true))
      (is (= (:size @terminal) [80 24]))
      (sut/close terminal)))
  (testing "get-terminalinal :text arg"
    (let [terminal (sut/get-terminal {:type :gui})]
      (is (= (map? @terminal) true))
      (is (= (:type @terminal) :gui))
      (is (= (:open @terminal) true))
      (is (= (:private @terminal) false))
      (is (vector? (:cursor @terminal)))
      (is (= (:cursor-visible @terminal) true))
      (is (= (:size @terminal) [80 24]))
      (sut/close terminal))))

(deftest cursor-position-tests
  (testing (str "set-cursor for " (:type @term) " type")
    (is (= (sut/set-cursor term 0 0) [0 0]))
    (is (= (sut/cursor-position term) [0 0]))
    (is (= (sut/set-cursor term 10 10) [10 10]))
    (is (= (sut/cursor-position term) [10 10]))))

(deftest colour-tests
  (testing (str "set colours for " (:type @term) " type")
    (is (= (:background @term) "DEFAULT"))
    (is (= (:foreground @term) "DEFAULT"))
    (sut/set-background term "BLACK")
    (sut/set-foreground term "WHITE")
    (is (= (:background @term) "BLACK"))
    (is (= (:foreground @term) "WHITE"))
    (sut/set-background term "DEFAULT")
    (sut/set-foreground term "DEFAULT")
    (is (= (:background @term) "DEFAULT"))
    (is (= (:foreground @term) "DEFAULT"))))

(deftest cursor-visible-test
  (testing (str "toggle cursor visibility for " (:type @term) " type")
    (is (= (:cursor-visible @term) true))
    (sut/toggle-cursor-visible term)
    (is (= (:cursor-visible @term) false))
    (sut/toggle-cursor-visible term)
    (is (= (:cursor-visible @term) true))))

(deftest clear-test
  (testing (str "clear terminal test for " (:type @term) " type")
    (sut/clear term)
    (is (= (:cursor @term) [0 0]))))

(deftest private-mode-test
  (testing (str "private mode test for " (:type @term) " type")
    (is (= (:private @term) false))
    (sut/toggle-private-mode term)
    (is (= (:private @term) true))))

(deftest write-tests
  (testing (str "writing to terminal tests for " (:type @term) " type")
    (when (= (:type @term) :gui)
      (sut/toggle-private-mode term)
      (sut/clear term)
      (sut/set-cursor term 5 5)
      (sut/put-char term \T)
      (is (= (:char (sut/get-char term 5 5)) \T))
      (sut/set-cursor term 5 6)
      (sut/put-string term "test")
      (let [s (string/join "" (map #(:char (sut/get-char term % 6)) [5 6 7 8]))]
        (is (= s "test")))
      (sut/put-string term "TEST" 5 7)
      (let [s (string/join "" (map #(:char (sut/get-char term % 7)) [5 6 7 8]))]
        (is (= s "TEST")))
      (sut/toggle-private-mode term))))

(deftest test-terminal-functions
  (doseq [t [:gui :text]]
    (reset! term @(sut/get-terminal {:type t}))
    (cursor-position-tests)
    (colour-tests)
    (cursor-visible-test)
    (clear-test)
    (private-mode-test)
    (write-tests)
    (sut/close term)))

(defn test-ns-hook []
  (get-terminal-tests)
  (test-terminal-functions))
