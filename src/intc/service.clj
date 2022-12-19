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