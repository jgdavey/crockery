(ns crockery.specs-test
  (:require [crockery.specs]
            [crockery.core :as crock]
            [clojure.spec.test.alpha :as stest]
            [clojure.test :as t :refer [deftest]]
            [clojure.spec.alpha :as s]
            [clojure.string :as str]))

(defn report-spec-results [check-results]
  (let [checks-passed? (->> check-results (map :failure) (every? nil?))]
    (if checks-passed?
      (t/do-report {:type    :pass
                    :message (str "Generative tests pass for "
                                  (str/join ", " (map :sym check-results)))})
      (doseq [failed-check (filter :failure check-results)]
        (let [r       (stest/abbrev-result failed-check)
              failure (:failure r)]
          (t/do-report
           {:type     :fail
            :message  (with-out-str (s/explain-out failure))
            :expected (->> r :spec rest (apply hash-map) :ret)
            :actual   (if (instance? Throwable failure)
                        failure
                        (::stest/val failure))}))))
    checks-passed?))

(deftest test-table-spec
  (report-spec-results
   (stest/check `crock/table {:clojure.spec.test.check/opts {:num-tests 20}})))
