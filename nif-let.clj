(defmacro nlet-or-else
  "If all bindings are non-nil, execute body in the context of those bindings.
  If a binding is nil, evaluate its `or-else` form.

  bindings* => binding-form or-else"
  [bindings & body]
  (cond
    (= (count bindings) 0) `(do ~@body)
    (symbol? (bindings 0)) `(if-let ~(subvec bindings 0 2)
                              (nlet-or-else ~(subvec bindings 3) ~@body)
                              ~(bindings 2))
    :else (throw (IllegalArgumentException. "symbols only in bindings"))))

(nlet-or-else
  [a (first [1 2 3]) :a-nil
   b nil (println "b is nil")
   c (+ a b) :c-nil]
  (+ a b c))

