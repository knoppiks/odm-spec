(ns odm.study-test
  (:require
    #?@(:clj
        [[clojure.spec.alpha :as s]
         [clojure.spec.test.alpha :as st]
         [clojure.test :refer :all]
         [odm-spec.test-util :refer [given-problems]]]
        :cljs
        [[cljs.spec.alpha :as s]
         [cljs.spec.test.alpha :as st]
         [cljs.test :refer-macros [deftest testing is are]]
         [odm-spec.test-util :refer-macros [given-problems]]])
         [odm.study]
         [odm-spec.util :as u]))

(st/instrument)

(deftest study-test
  (testing "Valid studies"
    (are [x] (s/valid? :odm/study x)
      #:odm.study
          {:oid "S01"
           :name "foo"
           :description "bar"
           :protocol-name "baz"}

      #:odm.study
          {:oid "S01"
           :name "foo"
           :description "bar"
           :protocol-name "baz"
           :measurement-units
           [#:odm.measurement-unit
               {:oid "U01"
                :name "kilogram"
                :symbol [{:lang-tag "de" :text "kg"}]}]}

      #:odm.study
          {:oid "S01"
           :name "foo"
           :description "bar"
           :protocol-name "baz"
           :metadata-versions
           [#:odm.metadata-version
               {:oid "V01"
                :name "foo"}]}))

  (testing "Missing keys"
    (given-problems :odm/study
      {}
      [first :pred] := `(fn [~'%] (contains? ~'% :odm.study/oid))
      [second :pred] := `(fn [~'%] (contains? ~'% :odm.study/name))
      [#(nth % 2) :pred] := `(fn [~'%] (contains? ~'% :odm.study/description))
      [#(nth % 3) :pred] := `(fn [~'%] (contains? ~'% :odm.study/protocol-name))))

  (testing "Duplicate measurement unit OIDs"
    (given-problems :odm/study
      #:odm.study
          {:oid "S01"
           :name "foo"
           :description "bar"
           :protocol-name "baz"
           :measurement-units
           [#:odm.measurement-unit
               {:oid "U01"
                :name "kilogram"
                :symbol [{:lang-tag "de" :text "kg"}]}
            #:odm.measurement-unit
                {:oid "U01"
                 :name "kilogram"
                 :symbol [{:lang-tag "de" :text "kg"}]}]}
      [first :path] := [:odm.study/measurement-units]
      [first :pred] := `(partial u/distinct-values? :odm.measurement-unit/oid)))

  (testing "Duplicate metadata version OIDs"
    (given-problems :odm/study
      #:odm.study
          {:oid "S01"
           :name "foo"
           :description "bar"
           :protocol-name "baz"
           :metadata-versions
           [#:odm.metadata-version
               {:oid "V01"
                :name "foo"}
            #:odm.metadata-version
                {:oid "V01"
                 :name "foo"}]}
      [first :path] := [:odm.study/metadata-versions]
      [first :pred] := `(partial u/distinct-values? :odm.metadata-version/oid)))

  #?(:clj
     (testing "Generator available"
       (is (doall (s/exercise :odm/study 1))))))
