(ns shorty.handler
  (:import [clojure.lang Murmur3]
           [org.apache.commons.validator.routines UrlValidator])

  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer (wrap-defaults site-defaults)]
            [ring.util.response :as response]
            [environ.core :refer [env]]
            [clojure.tools.logging :as log]
            [clj-leveldb :as leveldb]))

(def ^:const DBDIR (env "dbname" "/tmp/shorty.db"))

(defn valid-url?
  [url]
  (let [v (UrlValidator. (into-array ["http" "https"]))
        valid? (.isValid v url)]
    (log/info (format "VALID-URL - valid?: %s url: %s" valid? url))
    valid?))

(defn hash-url
  "Hash URL using clojure.lang.Murmur3"
  [url]
  (when (valid-url? url)
    (let [hfn (comp (partial format "%x")
                    #(Murmur3/hashUnencodedChars %))
          out (hfn url)]
      (log/info (format "url: %s\thashed: %s" url out))
      out)))

(defn dbconn []
  (leveldb/create-db DBDIR {:key-decoder byte-streams/to-string 
                            :val-decoder byte-streams/to-string}))

(defn save-url [url]
  "Save URL, using the hash as the key, if it doesn't exist already.
  Returns the hash regardless of the existence, in Redis"
  (when-let [hash-code (hash-url url)]
    (let [db (dbconn)]
      (leveldb/put db hash-code url)
      (.close db))
    hash-code))


(defn find-hashed [hashcode]
  (let [db (dbconn)
        res (leveldb/get db hashcode)]
    (.close db)
    res))

(defroutes app-routes
  (GET "/" [] "Hello World")
  (GET "/:id" [id]
       (if-let [x (find-hashed id)]
         (do
           (log/info (format "Found %s => %s  --- Redirecting :-(  " id x))
           (response/redirect x))
         {:status 404 :body "I Couldn't find that URL in my bag-o-tricks"}))
  (POST "/x"
        {params :params}
        (if-let [url (:url params)]
          (let [hashed (save-url url)]
            (format "Your new url is <a href=\"%s\">%s</a>" url hashed))
            {:status 500 :body "You gave me junk, I have nothing for you"}))
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes (assoc-in site-defaults [:security :anti-forgery] false)))
