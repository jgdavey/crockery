(ns crockery.fixed
  (:require
   [clojure.string :as str]
   [crockery.protocols :as p]
   [crockery.strings :as strings]
   [crockery.util :refer [align-cell]]
   #?(:clj [crockery.terminal :as term])))

(defn- find-coords [lines char1 char2]
  (first (keep-indexed
          (fn [i line]
            (when-let [x (str/index-of line char1)]
              (when-let [y (str/index-of line char2)]
                [i x y])))
          lines)))

(defn- extract [line x y]
  {:l (subs line 0 x)
   :m (subs line (inc x) y)
   :r (subs line (inc y))
   :x (subs line x (inc x))})

(defn parse-format [s]
  (let [lines (vec (if (string? s) (str/split-lines s) s))
        _ (assert (apply = (map count lines)) "All lines must be equal length")
        [header-line a b] (find-coords lines "A" "B")
        [data-line c d] (find-coords lines "C" "D")
        [more-data-line _ _] (find-coords lines "E" "F")]
    (if header-line
      (do
        (assert (#{0 1} header-line) "Header must be in first 2 lines")
        (assert (#{1 2} (- data-line header-line)) "Data is too many lines away from header"))
      (assert (#{0 1} data-line) "Data or header must be in first 2 lines"))
    {:table-top (when (= 1 header-line)
                  (extract (nth lines 0) a b))
     :header (when header-line
               (assoc
                (extract (nth lines header-line) a b)
                :x " "))
     :header-separator (when (and header-line (> (- data-line header-line) 1))
                         (extract (nth lines (inc header-line)) a b))
     :data (assoc (extract (nth lines data-line) c d)
                  :x " ")
     :data-separator (when (and more-data-line
                                (> (- more-data-line data-line) 1))
                       (extract (nth lines (inc data-line)) c d))
     :table-bottom (when (> (count lines)
                            (inc (or more-data-line data-line)))
                     (extract (last lines) c d))}))

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
   (fn [i col]
     (let [decimal-info (when (= :decimal (:align col))
                          (reduce
                           (fn [acc row]
                             (let [cell (nth row i)
                                   len (or (count cell) 0)
                                   pos (or (str/index-of cell ".") len)]
                               (doto
                                (-> acc
                                    (update :pre max pos)
                                    (update :post max (- len pos)))
                                 (tap>))))
                           {:pre 0 :post 0}
                           (rest pre-rendered)))
           width (or (:width col)
                     (when (:post decimal-info)
                       (max (count (nth (first pre-rendered) i))
                            (+ (:pre decimal-info) (:post decimal-info))))
                     (calculate-width (map #(nth % i) pre-rendered)))]
       (assoc col
              :decimal-info decimal-info
              :user-width (:width col)
              :width width)))
   cols))

(defn rebalance-widths [colspecs chromeless-max-width]
  (if (<= (apply + (map :width colspecs)) chromeless-max-width)
    colspecs
    (let [resize-user-widths (> (apply + (map #(or (:user-width %) 3) colspecs))
                                chromeless-max-width)]
      (loop [out []
             remaining colspecs
             remaining-width chromeless-max-width]
        (if (empty? remaining)
          out
          (let [colspec (first remaining)
                per-col (quot remaining-width (count remaining))
                new-col (if (and (> (:width colspec) per-col)
                                 (or resize-user-widths (nil? (:user-width colspec))))
                          (assoc colspec
                                 :width per-col
                                 :ellipsis true)
                          colspec)]
            (recur
             (conj out new-col)
             (next remaining)
             (- remaining-width (:width new-col)))))))))

(defn th [col s]
  (align-cell col
              s
              (:title-align col)))

(defn td [col s]
  (align-cell col
              s
              (:align col)))

(defrecord FixedWidthRender [escape chrome chrome-width postprocess]
  p/RenderTable
  (render-table [_ opts cols data]
    (let [cell-fns (into []
                         (map (fn [col]
                                (let [key-fn (:key-fn col)
                                      render-cell (:render-cell col)]
                                  #(-> % key-fn render-cell escape))))
                         cols)
          max-width (or (:max-width opts)
                        #?(:clj (term/detect-terminal-width))
                        #_200) ;; TODO should this default?
          rendered-headers (for [col cols]
                             (-> col :title escape))
          rendered-rows (for [row data]
                          (for [cell-fn cell-fns]
                            (cell-fn row)))
          colspecs (columns-with-widths cols (cons rendered-headers rendered-rows))
          colspecs (if max-width
                     (rebalance-widths colspecs (- max-width (chrome-width (count cols))))
                     colspecs)
          top        (when-let [{:keys [l m r x]} (:table-top chrome)]
                       (str l (str/join m (map #(apply str (repeat (:width %) x)) colspecs)) r))
          header     (when-let [{:keys [l m r]} (:header chrome)]
                       (str l (str/join m (map th colspecs rendered-headers)) r))
          header-sep (when-let [{:keys [l m r x]} (:header-separator chrome)]
                       (str l (str/join m (map #(apply str (repeat (:width %) x)) colspecs)) r))
          data-lines (let [{dl :l dm :m dr :r} (:data chrome)
                           data-rows (for [row rendered-rows]
                                       (str dl (str/join dm (map td colspecs row)) dr))]
                       (if (:data-separator chrome)
                         (let [{:keys [l m r x]} (:data-separator chrome)
                               sep (str l (str/join m (map #(apply str (repeat (:width %) x)) colspecs)) r)]
                           (interpose sep data-rows))
                         data-rows))
          bottom     (when-let [{:keys [l m r x]} (:table-bottom chrome)]
                       (str l (str/join m (map #(apply str (repeat (:width %) x)) colspecs)) r))]
      (->> (concat [top header header-sep]
                   data-lines
                   [bottom])
           (remove nil?)
           (postprocess colspecs)))))

(defn make-renderer [{:keys [chrome escape postprocess]}]
  (let [chrome-data (:data chrome)
        sides (+ (count (:l chrome-data))
                 (count (:r chrome-data)))
        mid (count (:m chrome-data))
        chrome-width-fn (fn [cols]
                          (+ sides (* (dec cols) mid)))]
    (map->FixedWidthRender {:escape (comp strings/escape (or escape identity))
                            :chrome chrome
                            :chrome-width chrome-width-fn
                            :postprocess (or postprocess (fn [_ lines] lines))})))

(defmacro deffixed [sym format & {:keys [escape postprocess]}]
  (let [docstring (str "Format according to the following format spec:\n\n"
                       (if (string? format)
                         format
                         (str/join "\n" format)))]
    `(let [format# ~format]
       (def ~sym
         ~docstring
         (make-renderer
          {:escape ~escape
           :postprocess ~postprocess
           :chrome (parse-format format#)})))))
