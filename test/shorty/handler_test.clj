(ns shorty.handler-test
  (:use midje.sweet)
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [shorty.handler :refer :all]))


(let [response (app (mock/request :get "/"))]
  (fact "Testing main route"
    (:status response) => 200
    (:body response) => (str "Hello World"))

  (fact "Testing hashing function"
    (hash-url "http://www.example.com") => "cfce4929"  ))

