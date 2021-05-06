(ns crockery.core-test
  (:require [clojure.test :as test :refer [deftest is testing]]
            [clojure.string :as str]
            [crockery.protocols :as p]
            [crockery.core :as crock]))

(declare table?)

(defmethod test/assert-expr 'table? [msg form]
  ;; Test if x is an instance of y.
  `(let [actual# ~(nth form 1)
         expected# ~(nth form 2)
         joined# (cond (string? expected#) expected#
                       (sequential? expected#) (clojure.string/join "\n" expected#))]
     (let [result# (= actual# joined#)]
       (if result#
         (test/do-report {:type :pass, :message ~msg,
                          :expected joined#, :actual actual#})
         (test/do-report {:type :fail, :message ~msg,
                          :expected joined#, :actual actual#}))
       result#)))

(defn table-as-string [& args]
  (str/replace
   (with-out-str
     (apply crock/print-table args))
   #"\n$" ""))

(def people [{:name "Alice" :age 29 :joined #inst "2019-03-01"}
             {:name "Bob" :age 22 :joined #inst "2020-10-29"}
             {:name "Charlotte" :age 42 :joined #inst "2014-04-17"}])


(deftest test-table-no-options
  (let [rendered (table-as-string people)]
    (is (table? rendered
                ["|-----------+-----+----------------------|"
                 "| Name      | Age | Joined               |"
                 "|-----------+-----+----------------------|"
                 "| Alice     | 29  | 2019-03-01T00:00:00Z |"
                 "| Bob       | 22  | 2020-10-29T00:00:00Z |"
                 "| Charlotte | 42  | 2014-04-17T00:00:00Z |"
                 "|-----------+-----+----------------------|"]))))

(deftest test-table-with-column-keys
  (let [rendered (table-as-string [:age :name] people)]
    (is (table? rendered
                ["|-----+-----------|"
                 "| Age | Name      |"
                 "|-----+-----------|"
                 "| 29  | Alice     |"
                 "| 22  | Bob       |"
                 "| 42  | Charlotte |"
                 "|-----+-----------|"]))))

(deftest test-table-with-options-and-column-keys
  (let [rendered (table-as-string {} [:name :age] people)]
    (is (table? rendered
                ["|-----------+-----|"
                 "| Name      | Age |"
                 "|-----------+-----|"
                 "| Alice     | 29  |"
                 "| Bob       | 22  |"
                 "| Charlotte | 42  |"
                 "|-----------+-----|"]))))

(deftest test-table-arg-combinations
  (let [expected ["|-----------+-----|"
                  "| Name      | Age |"
                  "|-----------+-----|"
                  "| Alice     | 29  |"
                  "| Bob       | 22  |"
                  "| Charlotte | 42  |"
                  "|-----------+-----|"]]
    (is (table? (table-as-string {} [:name :age] people) expected))
    (is (table? (table-as-string {:columns [:name :age]} people) expected))
    (is (table? (table-as-string {:format :org
                                  :columns [{:name :name :title "Name"} :age]}
                                 people)
                expected))))

(deftest test-table-arg-combinations
  (let [expected ["|-----------+-----|"
                  "| Name      | Age |"
                  "|-----------+-----|"
                  "| Alice     | 29  |"
                  "| Bob       | 22  |"
                  "| Charlotte | 42  |"
                  "|-----------+-----|"]]
    (is (table? (table-as-string {} [:name :age] people) expected))
    (is (table? (table-as-string {:columns [:name :age]} people) expected))
    (is (table? (table-as-string {:format :org
                                  :columns [{:name :name :title "Name"} :age]}
                                 people)
                expected))))

(deftest test-alignment
  (testing "Title alignment can differ"
    (let [rendered (table-as-string [{:key-fn :name
                                      :title "First Name"
                                      :align :left
                                      :title-align :center
                                      :width 20}
                                     {:name :age
                                      :align :right}]
                                    people)]
      (is (table? rendered
                  ["|----------------------+-----|"
                   "|      First Name      | Age |"
                   "|----------------------+-----|"
                   "| Alice                |  29 |"
                   "| Bob                  |  22 |"
                   "| Charlotte            |  42 |"
                   "|----------------------+-----|"]))))
  (testing "Center 'favors' left side"
    (let [rendered (table-as-string [{:key-fn :name
                                      :title "First Name"
                                      :align :left
                                      :title-align :center
                                      :width 20}
                                     {:name :age
                                      :align :center
                                      :width 5}]
                                    people)]
      (is (table? rendered
                  ["|----------------------+-------|"
                   "|      First Name      |  Age  |"
                   "|----------------------+-------|"
                   "| Alice                |  29   |"
                   "| Bob                  |  22   |"
                   "| Charlotte            |  42   |"
                   "|----------------------+-------|"])))))

(deftest test-escaping
  (let [rendered (table-as-string [:a :b] [{:a "foo\nbar" :b "what\tever"}])]
    (is (table? rendered
                ["|----------+------------|"
                 "| A        | B          |"
                 "|----------+------------|"
                 "| foo\\nbar | what\\tever |" ;; lines up when printed
                 "|----------+------------|"]))))

(deftest test-empty-table
  (let [empty-table ["|--|" "|  |" "|--|" "|--|"]]
    (is (= empty-table (crock/table [])))
    (is (= empty-table (crock/table nil)))
    (is (= empty-table (crock/table [] [])))
    (is (= empty-table (crock/table [] nil)))
    (is (= empty-table (crock/table nil [])))
    (is (= empty-table (crock/table nil nil)))))

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
    (is (table? (table-as-string {:format :fancy} cols data) expected))
    (is (table? (table-as-string {:format :fancy, :columns cols} data) expected))
    (is (table? (binding [crock/*default-options* {:format :fancy}]
                  (table-as-string cols data))
                expected))))

(deftest test-tsv-format
  (let [data [{:q "When?" :a "tomorrow\t9pm"}
              {:q "Who?"  :a (reify p/RenderCell
                               (render-cell [_ _] "No\tAnswer"))}]
        cols [{:key-fn :q :title "question"} :a]
        expected ["question\tA"
                  "When?\ttomorrow\\t9pm"
                  "Who?\tNo\\tAnswer"]]
    (is (table? (table-as-string {:format :tsv} cols data) expected))
    (is (table? (binding [crock/*default-options* {:format :tsv}]
                  (table-as-string cols data))
                expected))))

(deftest test-gfm-format
  (testing "includes alignment hints"
    (let [expected ["|   Name    | Age | Joined               |"
                    "|:---------:|----:|:---------------------|"
                    "|   Alice   |  29 | 2019-03-01T00:00:00Z |"
                    "|   B\\|ob   |  22 | 2020-10-29T00:00:00Z |"
                    "| Charlotte |  42 | 2014-04-17T00:00:00Z |"]
          data (assoc-in people [1 :name]  "B|ob")
          cols [{:name :name :align :center}
                {:key-fn :age, :title "Age", :align :right}
                :joined]]
      (is (table? (table-as-string {:format :gfm} cols data) expected))
      (is (table? (binding [crock/*default-options* {:format :gfm}]
                    (table-as-string cols data))
                  expected)))))
