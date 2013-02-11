; http://stackoverflow.com/questions/6728567/best-way-to-lazily-collapse-multiple-contiguous-items-of-a-sequence-into-a-single

; Other solutions ----------
; fastest so far
(defn lazy-collapse
  ([coll is-collapsable-item? collapsed-item-representation] (lazy-collapse coll is-collapsable-item? collapsed-item-representation false))
  ([coll is-collapsable-item? collapsed-item-representation in-collapsable-segment?]
  (let [step (fn [coll in-collapsable-segment?]
               (when-let [item (first coll)]
                 (if (is-collapsable-item? item)
                   (if in-collapsable-segment?
                     (recur (rest coll) true)
                     (cons collapsed-item-representation (lazy-collapse (rest coll) is-collapsable-item? collapsed-item-representation true)))
                   (cons item (lazy-collapse (rest coll) is-collapsable-item? collapsed-item-representation false)))))]
    (lazy-seq (step coll in-collapsable-segment?)))))

(defn msmith [ss pred replacement]  
    (lazy-seq    
          (if-let [s (seq ss)]   
              (let [[f & rr] s]  
               (if (pred f)   
                   (cons replacement (msmith (drop-while pred rr) pred replacement))  
                   (cons f (msmith rr pred replacement)))))))

(defn eric-normand [l p v]
  (cond
    (nil? (seq l))
    nil
    (p (first l))
    (lazy-seq (cons v (eric-normand (drop-while p l) p v)))
    :otherwise
    (lazy-seq (cons (first l) (eric-normand (rest l) p v)))))

; My solutions ----------------

; my original solution
; has a bug!  try input: "a   \t\n   tree" -- eats double e!
(defn collapse-bug [col pred rep]
  (let [f (fn [x] (if (pred x) rep x))]
    (map (comp f first) (partition-by f col))))

; my corrected solution; still fairly pretty
(defn collapse-slow [col pred rep]
  (let [f (fn [[x & more :as xs]] (if (pred x) [rep] xs))]
    (mapcat f (partition-by #(if (pred %) true) col))))

; 3x faster than collapse-slow
(defn collapse [xs pred rep]
  (when-let [x (first xs)]
    (lazy-seq 
      (if (pred x)
        (cons rep (collapse (drop-while pred (rest xs)) pred rep))
        (cons x (collapse (rest xs) pred rep))))))

; test harness
(def is-wsp? #{\space \tab \newline \return})
(def test-str "\t    a\r          s\td  \t \r \n         f \r\n")

(println "slow:")
(time (dotimes [_ 1e6] (dorun (collapse-slow test-str is-wsp? \space))))
; "Elapsed time: 13484.177938 msecs"

(println "faster:")
(time (dotimes [_ 1e6] (dorun (collapse test-str is-wsp? \space))))
; "Elapsed time: 3662.257347 msecs"

#_(dorun
  (->> ["SuperHorst" lazy-collapse
        "Overthink" collapse
        "M Smith" msmith
        "Eric Normand" eric-normand]
    (partition 2)
    (map (fn [[fn-name f]] 
           (printf "%15s: " fn-name)
           (time (dotimes [_ 2e7] (last (f test-str is-wsp? \space))))))))

