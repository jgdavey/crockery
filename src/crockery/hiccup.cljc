(ns crockery.hiccup
  (:require [crockery.protocols :as p]
            [crockery.util :refer [column-xform]]))

(def renderer
  (reify
    p/RenderTable
    (render-table [_ cols data]
      [:table
       [:thead
        (into [:tr]
              (for [col cols]
                [:th {:align (name (:title-align col))}
                 (-> col :title)]))]
       [:tbody
        (for [row data]
          (into [:tr]
                (for [col cols]
                  [:td {:align (name (:align col))}
                   (-> ((:key-fn col) row)
                       (p/render-cell col))])))]])))

(defn table
  ([data]
   (table {} data))
  ([cols data]
   (let [cols (into [] column-xform cols)]
     (p/render-table renderer cols data))))
