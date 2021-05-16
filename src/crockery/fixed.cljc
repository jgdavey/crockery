(ns crockery.fixed
  (:require [crockery.protocols :as p]
            [crockery.strings :as strings]
            [crockery.util :refer [align-cell]]))

(defn maximum
  ([] 0)
  ([x] x)
  ([a b]
      (cond
        (nil? b) a
        (> b a) b
        :else a)))

(defn calculate-width [rows]
  (transduce (map count) maximum 0 rows))

(defn columns-with-widths [cols pre-rendered]
  (map-indexed
   (fn [i {:keys [width] :as col}]
     (assoc col
            :width (or width
                       (calculate-width (map #(nth % i) pre-rendered)))))
   cols))

(defrecord FixedWidthRender [th td assemble escape]
  p/RenderTable
  (render-table [_ cols data]
    (let [cell-fns (into []
                         (map (fn [col]
                                (let [key-fn (:key-fn col)
                                      render-cell (:render-cell col)]
                                  #(-> % key-fn render-cell escape))))
                         cols)
          rendered-headers (for [col cols]
                             (-> col :title escape))
          rendered-rows (for [row data]
                          (for [cell-fn cell-fns]
                            (cell-fn row)))
          colspecs (columns-with-widths cols (cons rendered-headers rendered-rows))]
      (assemble colspecs
                (map th colspecs rendered-headers)
                (for [row rendered-rows]
                  (map td colspecs row))))))

(defn aligned-th [col s]
  (align-cell col
              s
              (:title-align col)))

(defn aligned-td [col s]
  (align-cell col
              s
              (:align col)))

(defn make-renderer [{:keys [td th assemble escape]}]
  (map->FixedWidthRender {:td (or td (fn [_ s] s))
                          :th (or th (fn [_ s] s))
                          :escape (comp strings/escape (or escape identity))
                          :assemble assemble}))
