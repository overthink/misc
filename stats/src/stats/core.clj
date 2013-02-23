(ns stats.core)
(use '(incanter core stats charts))

(view (histogram (sample-exp 10000 :rate 1/300) :nbins 30))
