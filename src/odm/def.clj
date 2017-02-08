(ns odm.def
  (:require [clojure.spec :as s]
            [odm.data-formats :as df]))

;; The domain, origin, purpose and comment attributes carry submission
;; information as described in the CDISC Submission Metadata Model located in
;; the SDTM Metadata Submission Guidelines.

(s/def ::domain
  ::df/text)

(s/def ::origin
  ::df/text)

(s/def ::purpose
  ::df/text)

(s/def ::comment
  ::df/text)
