(ns intc.service
  (:require [io.pedestal.http :as http]
            [io.pedestal.interceptor :as i]
            [io.pedestal.interceptor.chain :as chain]
            [io.pedestal.interceptor.error :as err]))

(def routes #{})

(defn start
  []
  (-> {::http/port   8822
       ::http/join?  false
       ::http/type   :jetty
       ::http/routes routes}
      http/create-server
      http/start))

(def say-hello
  {:name  ::say-hello
   :enter (fn [context]
            (assoc context :response {:body   "Hello, world!"
                                      :status 200}))})

(def odds
  (i/interceptor
    {:name  ::odds
     :enter (fn [context]
              (assoc context :response {:body   "I handle odd numbers\n"
                                        :status 200}))}))

(def evens
  (i/interceptor
    {:name  ::evens
     :enter (fn [context]
              (assoc context :response {:body   "Even numbers are my bag\n"
                                        :status 200}))}))

;; code with exception handling.
(def chooser
  {:name  ::chooser
   :enter (fn [context]
            (try
              (let [param (get-in context [:request :query-params :n])
                    n (Integer/parseInt param)
                    nxt (if (even? n) evens odds)]
                (chain/enqueue context [nxt]))
              (catch NumberFormatException e
                (assoc context :response {:body   "Not a number!\n"
                                          :status 400}))))})

(def routes
  #{["/hello" :get say-hello]
    ["/data-science" :get chooser]})

;; code without exception handling.
(def chooser2
  {:name  ::chooser
   :enter (fn [context]
            (let [n (-> context :request :query-params :n Integer/parseInt)
                  nxt (if (even? n) evens odds)]
              (chain/enqueue context [nxt])))})

(def routes
  #{["/hello" :get say-hello]
    ["/data-science" :get chooser2]})


;(def attach-guid
;  {:name  ::attach-gui
;   :enter (fn [context] (assoc context ::guid (random-uuid)))})

(def ^:private database (atom nil))

;(def db-interceptor
;  {:name  ::database-interceptor
;   :enter (fn [context]
;            (update context :request assoc ::database @database))
;   :leave (fn [context]
;            (if-let [[op & args] (::tx-data context)]
;              (do
;                (apply swap! database op args)
;                (assoc-in context [:request ::database] @database))
;              context))})
;
;(defn attach-database [uri]
;  (let [conn (db/connect uri)]
;    {:name  ::attach-database
;     :enter #(assoc % ::connection conn ::db (d/db conn))}))

(defn call-auth-system [context]
  {:body   "Even numbers are my bag\n"
   :status 200})

;(def third-party-auth
;  {:name  ::third-party-auth
;   :enter (fn [context]
;            (if (:session context)
;              context
;              (go
;                (assoc context :auth-response (call-auth-system context)))))})