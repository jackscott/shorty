(ns shorty.handler-test
  (:use midje.sweet)
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [shorty.handler :refer :all]))


(let [response (app (mock/request :get "/"))]
  (fact "Testing main route"
    (:status response) => 200
    (:body response) => (str "Hello World")))

;; (deftest test-app
;;   (testing "main route"
;;     (let [response (app (mock/request :get "/"))]
;;       (is (= (:status response) 200))
;;       (is (= (:body response) "Hello World"))))

;;   (testing "not-found route"
;;     (let [response (app (mock/request :get "/invalid"))]
;;       (is (= (:status response) 404)))))
