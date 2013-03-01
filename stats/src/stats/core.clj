(ns stats.core
  "Review some stats I should already know."
  (:use (incanter core stats charts)))

;; General stuff (I'm dumb)
;; - if you have a data set, you can always calculate mean, variance, stddev,
;;   quartiles, inter-quartile range, etc.  and it's always done the same way.
;;   - e.g. you don't need to "know the distribution" to calculate stddev
;;   - this sounds dumb to state, but I get confused when I have some things
;;     analyzing real data sets (desriptive stats) and others modelling data
;;     using various distributions (inferrential stats)
;; - z-score normalizes by stddev -- compare values from two random variables
;; - "outlier" is actually defined:
;;   http://en.wikipedia.org/wiki/Interquartile_range#Interquartile_range_and_outliers

;; Generating values in a particular distribution
;; - http://en.wikipedia.org/wiki/Inverse_transform_sampling_method
;; - i.e. use the distribution's quantile function
;; - quantile is the inverse of the cumulative distribution function
;;   - take uniform random values and make them distributed the way you want


;; Poisson distribution
;; - you know the mean number of successes in a time period ("3 times a day")
;; - you want to know the probability of a different number of successes in a
;;   future time period ("how likely are 4 successes tomorrow?")
;;   - (I say "time", but it doesn't have to be time, could be length or volume
;;     or something else)
;; - in a very small time window the probability of success is basically 0
;; - in a very large time window the probability of success is basically 1

(view (histogram (take 50 (repeatedly rand))))

(view (box-plot (sample-exp 10000 :rate 1/300) :nbins 30))

