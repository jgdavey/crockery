(ns crockery.hiccup-test
  (:require [crockery.hiccup :as hiccup]
            [clojure.test :as t :refer [deftest is]]))

(deftest table-test
  (let [data [{:company "EvilCo" :created #inst "2019-03-01"}
              {:company "Silly, Inc." :created #inst "2018-12-31"}]]
    (is (=
         [:table
          [:thead
           [:tr
            [:th {:align "right"} "Company"]
            [:th {:align "left"} "Created"]]]
          [:tbody
           (list
            [:tr
             [:td {:align "center"} "EvilCo"]
             [:td {:align "left"} "2019-03-01T00:00:00Z"]]
            [:tr
             [:td {:align "center"} "Silly, Inc."]
             [:td {:align "left"} "2018-12-31T00:00:00Z"]])]]
         (hiccup/table [{:name :company
                         :align :center
                         :title-align :right}
                        :created] data)))))
(deftest table-no-titles-test
  (let [data [{:company "EvilCo" :created #inst "2019-03-01"}
              {:company "Silly, Inc." :created #inst "2018-12-31"}]]
    (is (=
         [:table
          [:tbody
           (list
            [:tr
             [:td {:align "center"} "EvilCo"]
             [:td {:align "left"} "2019-03-01T00:00:00Z"]]
            [:tr
             [:td {:align "center"} "Silly, Inc."]
             [:td {:align "left"} "2018-12-31T00:00:00Z"]])]]
         (hiccup/table {:titles? false}
                       [{:name :company
                         :align :center
                         :title-align :right}
                        :created]
                       data)))))
