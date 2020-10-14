(ns theophilusx.citronella.terminal-test
  (:require [theophilusx.citronella.terminal :as sut]
            [clojure.test :refer [deftest testing is]]))

(deftest get-terminal-tests
  (testing "get-terminal no args"
    (let [term (sut/get-terminal)]
      (is (= (map? @term) true))
      (is (= (:type @term) :auto))
      (is (= (:open @term) true))
      (is (= (:private @term) false))
      (is (= (:cursor @term) [0 0]))
      (is (= (:cursor-visible @term) true))
      (is (= (:size @term) [80 24]))
      (sut/close term)))
  (testing "get-terminal :auto arg"
    (let [term (sut/get-terminal {:type :auto})]
      (is (= (map? @term) true))
      (is (= (:type @term) :auto))
      (is (= (:open @term) true))
      (is (= (:private @term) false))
      (is (= (:cursor @term) [0 0]))
      (is (= (:cursor-visible @term) true))
      (is (= (:size @term) [80 24]))
      (sut/close term)))
  (testing "get-terminal :text arg"
    (let [term (sut/get-terminal {:type :text})]
      (is (= (map? @term) true))
      (is (= (:type @term) :text))
      (is (= (:open @term) true))
      (is (= (:private @term) false))
      (is (= (:cursor @term) [0 0]))
      (is (= (:cursor-visible @term) true))
      (is (= (:size @term) [80 24]))
      (sut/close term)))
  (testing "get-terminal :text arg"
    (let [term (sut/get-terminal {:type :gui})]
      (is (= (map? @term) true))
      (is (= (:type @term) :gui))
      (is (= (:open @term) true))
      (is (= (:private @term) false))
      (is (= (:cursor @term) [0 0]))
      (is (= (:cursor-visible @term) true))
      (is (= (:size @term) [80 24]))
      (sut/close term))))

