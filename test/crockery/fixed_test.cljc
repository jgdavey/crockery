(ns crockery.fixed-test
  (:require
   [clojure.test :as t :refer [deftest is]]
   [crockery.fixed :as fixed]))

(deftest test-rebalance-widths
  (let [cols [{:width 10}
              {:width 15}]]
    (is (=
         [{:width 10} {:width 15}]
         (fixed/rebalance-widths cols 25)))
    (is (=
         [{:width 10} {:width 10 :ellipsis true}]
         (fixed/rebalance-widths cols 20)))
    (is (=
         [{:width 14 :user-width 14} {:width 7 :ellipsis true}]
         (fixed/rebalance-widths [{:width 14 :user-width 14} {:width 12}] 21)))
    (is (=
         [{:width 10 :user-width 14 :ellipsis true}
          {:width 11 :user-width 12 :ellipsis true}]
         (fixed/rebalance-widths [{:width 14 :user-width 14}
                                  {:width 12 :user-width 12}] 21)))))

(deftest test-parse-format
  (let [chrome {:l "|-"
                :m "-|-"
                :r "-|"
                :x "-"}
        data {:l "| "
              :m " | "
              :r " |"
              :x " "}]
    (let [input-format ["|---|---|"
                        "| A | B |"
                        "| C | D |"
                        "| E | F |"
                        "|---|---|"]]
      (is (= {:table-top chrome
              :header data
              :header-separator nil
              :data data
              :data-separator   nil
              :table-bottom chrome}
             (fixed/parse-format input-format))))
    (let [input-format ["|---|---|"
                        "| A | B |"
                        "| C | D |"
                        "|---|---|"
                        "| E | F |"
                        "|---|---|"]]
      (is (= {:table-top chrome
              :header data
              :header-separator nil
              :data data
              :data-separator chrome
              :table-bottom chrome}
             (fixed/parse-format input-format))))
    (let [input-format ["|---|---|"
                        "| A | B |"
                        "|---|---|"
                        "| C | D |"
                        "|---|---|"]]
      (is (= {:table-top chrome
              :header data
              :header-separator chrome
              :data data
              :data-separator nil
              :table-bottom chrome}
             (fixed/parse-format input-format))))
    (let [input-format ["| A | B |"
                        "| C | D |"]]
      (is (= {:table-top nil
              :header data
              :header-separator nil
              :data data
              :data-separator nil
              :table-bottom nil}
             (fixed/parse-format input-format))))))
