; Simulation to demonstrate/solve quesiton posted by Notch:
;
; "Let's say we have a fair lottery where a hundred million people get one
; ticket each, and a winner is drawn at random each day. The winner stays in
; the pool even after winning. What are the odds that you win it once before
; someone else wins it three times (doesn't have to be in a row)?"
;
; https://plus.google.com/116872576248355504859/posts/CfTCkstHuhk
; http://www.reddit.com/r/math/comments/iyicr/notch_posted_this_interesting_probability/

;; WARNING: this takes a ridiculous amount of memory!

(def N 100000000) ; number of people in lottery; assume "I" am player 0

(defn draw 
  "Select a random integer in the range [0, n)."
  [n]
  (int (rand n)))

(defn update-winner 
  "Increment the win count for winner in sate.  Return updated state."
  [winner state]
  (update-in state [winner] (fn [x] (if (nil? x) 1 (inc x)))))
  ;[(update-in (merge state {winner 1}) [winner] inc) (state winner)])

(defn sim
  "The simulation ends when either I win, or someone else wins 3 times.  If I
  win, the simulation is a success, otherwise it's a failure."
  [state]
  (let [winner (draw N)
        newstate (update-winner winner state)
        wins (newstate winner)]
    #_(println "winner, wins: " winner ", " wins)
    (cond
      (zero? winner) true
      (= 3 wins) false
      :else (recur newstate))))

(let [numsims 1000
      wins (->> (take 1000 (repeatedly #(sim {}))) (filter true?) count)]
  (printf "%d players\n" N)
  (printf "%d trials\n" numsims)
  (println (/ wins numsims))
  (printf "%.2f%%\n" (double (* 100 (/ wins numsims)))))

