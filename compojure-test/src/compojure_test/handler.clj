(ns compojure-test.handler)

(defn print-request
  "Trying out a ring handler."
  [request]
  {:status 200
   :headers {"Content-Type" "text/plain"}
   :body (pr-str request)})
   ;; pretty print is incredibly expensive!
   ;;:body (with-out-str (clojure.pprint/pprint request))})

(defn add-test-header
  "Middleware that adds a nonsense header."
  [handler]
  (fn [request]
    (let [response (handler request)]
      (assoc-in response [:headers "X-Whatev"] "42"))))

