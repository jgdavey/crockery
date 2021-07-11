(ns crockery.hiccup
  (:require [crockery.protocols :as p]
            [crockery.util :refer [normalize-column to-column-map]]))

(def renderer
  (reify
    p/RenderTable
    (render-table [_ _opts cols data]
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
   (let [cols (into [] (comp (map to-column-map)
                             (map normalize-column))
                    cols)]
     (p/render-table renderer {} cols data))))
