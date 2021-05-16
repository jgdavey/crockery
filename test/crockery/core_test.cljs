(ns crockery.core-test
  (:require [clojure.test :as test :refer [deftest is testing]]
            [clojure.string :as str]
            [crockery.protocols :as p]
            [crockery.core :as crock]))

(def people [{:name "Alice" :age 29 :joined #inst "2019-03-01"}
             {:name "Bob" :age 22 :joined #inst "2020-10-29"}
             {:name "Charlotte" :age 42 :joined #inst "2014-04-17"}])


(deftest test-table-no-options
  (let [rendered (crock/table people)]
    (is (= rendered
           ["|-----------+-----+--------------------------|"
            "| Name      | Age | Joined                   |"
            "|-----------+-----+--------------------------|"
            "| Alice     | 29  | 2019-03-01T00:00:00.000Z |"
            "| Bob       | 22  | 2020-10-29T00:00:00.000Z |"
            "| Charlotte | 42  | 2014-04-17T00:00:00.000Z |"
            "|-----------+-----+--------------------------|"]))))

(deftest test-fancy-format-table
  (let [data [{:foo "what" :bar 4}
              {:foo "who"  :bar 87}]
        cols [{:name :foo} {:name :bar :width 9}]
        expected ["┌──────┬───────────┐"
                  "│ Foo  │ Bar       │"
                  "├──────┼───────────┤"
                  "│ what │ 4         │"
                  "│ who  │ 87        │"
                  "└──────┴───────────┘"]]
    (is (= (crock/table {:format :fancy} cols data) expected))
    (is (= (crock/table {:format :fancy, :columns cols} data) expected))
    (is (= (binding [crock/*default-options* {:format :fancy}]
             (crock/table cols data))
           expected))))

(deftest test-tsv-format
  (let [data [{:q "When?" :a "tomorrow\t9pm"}
              {:q "Who?"  :a (reify p/RenderCell
                               (render-cell [_ _] "No\tAnswer"))}]
        cols [{:key-fn :q :title "question"} :a]
        expected ["question\tA"
                  "When?\ttomorrow\\t9pm"
                  "Who?\tNo\\tAnswer"]]
    (is (= (crock/table {:format :tsv} cols data)) expected)
    (is (= (binding [crock/*default-options* {:format :tsv}]
             (crock/table cols data))
           expected))))

(deftest test-gfm-format
  (testing "includes alignment hints"
    (let [expected ["|   Name    | Age | Joined                   |"
                    "|:---------:|----:|:-------------------------|"
                    "|   Alice   |  29 | 2019-03-01T00:00:00.000Z |"
                    "|   B\\|ob   |  22 | 2020-10-29T00:00:00.000Z |"
                    "| Charlotte |  42 | 2014-04-17T00:00:00.000Z |"]
          data (assoc-in people [1 :name]  "B|ob")
          cols [{:name :name :align :center}
                {:key-fn :age, :title "Age", :align :right}
                :joined]]
      (is (= (crock/table {:format :gfm} cols data) expected))
      (is (= (binding [crock/*default-options* {:format :gfm}]
               (crock/table cols data))
             expected)))))

(deftest test-default-column-options
  (let [rendered (crock/table {:defaults {:align :center}}
                              [:a :b]
                              [{:a "foo\nbar" :b "what\tever"}])]
    (is (=  rendered
            ["|----------+------------|"
             "|    A     |     B      |"
             "|----------+------------|"
             "| foo\\nbar | what\\tever |" ;; lines up when printed
             "|----------+------------|"])))
  (let [rendered (crock/table {:defaults {:align :center
                                          :render-title pr-str
                                          :title-align :left}}
                              [{:name :a, :title-align :right} :b]
                              [{:a "foo\nbar" :b "what\tever"}])]
    (is (= rendered
           ["|----------+------------|"
            "|       :a | :b         |"
            "|----------+------------|"
            "| foo\\nbar | what\\tever |" ;; lines up when printed
            "|----------+------------|"]))))
