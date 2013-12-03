
(defmacro nif-let
  "If all bindings are non-nil, execute body in the context of those bindings.
  If a binding is nil, evaluate its `or-else` form.

  bindings* => binding-form or-else"
  [bindings & body]
  (cond
    (= (count bindings) 0) `(do ~@body)
    (symbol? (bindings 0)) `(if-let ~(subvec bindings 0 2)
                              (nif-let ~(subvec bindings 3) ~@body)
                              ~(bindings 2))
    :else (throw (IllegalArgumentException. "symbols only in bindings"))))

(nif-let
  [a (first [1 2 3]) :a-nil
   b nil (println "b is nil")
   c (+ a b) :c-nil]
  (+ a b c))

(let [x 10
      y 20
      z nil
      w 40]
  #_(if-let [a x]
    (if-let [b y]
      (if-let [c z]
        (if-let [d w]
          (+ a b c d)
          :d-is-nil)
        :c-is-nil)
      :b-is-nil)
    :a-is-nil)

  #_(nif-let [a x :a-is-nil
            b y :b-is-nil
            c z :c-is-nil
            d w :d-is-nil]
           (+ a b c d))

  (let [fail (fn [x] {:status :failed :value x})
        ok (fn [x] {:status :ok :value x})]
    (nif-let [a x (fail :a-is-nil)
              b y (fail :b-is-nil)
              c z (fail :c-is-nil)
              d w (fail :d-is-nil)]
             (ok (+ a b c d))))

  )
