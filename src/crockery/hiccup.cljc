(ns crockery.hiccup
  (:require [crockery.protocols :as p]
            [crockery.util :refer [normalize-args]]))

(def renderer
  (reify
    p/RenderTable
    (render-table [_ opts cols data]
      (reduce into [:table]
              [(when (:titles? opts)
                 [[:thead
                   (into [:tr]
                         (for [col cols]
                           [:th {:align (name (:title-align col))}
                            (-> col :title)]))]])
               [[:tbody
                 (for [row data]
                   (into [:tr]
                         (for [col cols]
                           [:td {:align (name (:align col))}
                            (-> ((:key-fn col) row)
                                (p/render-cell col))])))]]]))))

(defn table
  ([data]
   (table {} nil data))
  ([cols data]
   (table {} cols data))
  ([opts cols data]
   (let [[opts cols data] (normalize-args {} opts cols data)]
     (p/render-table renderer opts cols data))))
