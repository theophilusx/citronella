(ns theophilusx.citronella.terminal-test
  (:require [theophilusx.citronella.terminal :as sut]
            [clojure.test :refer [deftest testing is]]))

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
    (is (= (:background @term) :default))
    (is (= (:foreground @term) :default))
    (sut/set-background term :black)
    (sut/set-foreground term :white)
    (is (= (:background @term) :black))
    (is (= (:foreground @term) :white))
    (sut/set-background term :default)
    (sut/set-foreground term :default)
    (is (= (:background @term) :default))
    (is (= (:foreground @term) :default))))

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

(deftest test-terminal-functions
  (doseq [t [:gui :text]]
    (reset! term @(sut/get-terminal {:type t}))
    (cursor-position-tests)
    (colour-tests)
    (cursor-visible-test)
    (clear-test)
    (private-mode-test)
    (sut/close term)))

(defn test-ns-hook []
  (get-terminal-tests)
  (test-terminal-functions))
