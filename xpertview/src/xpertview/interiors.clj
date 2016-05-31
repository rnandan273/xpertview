(ns xpertview.interiors
  (:require [compojure.core :refer [defroutes GET]]
            [ring.util.http-response :refer [ok]]
            [clojure.java.io :as io]
            [incanter.core :as icore]
            [datomic.api :as d]
            ;[datomic.api :only [q db] :as d]
            [incanter.stats :as istats]
            [incanter.io :as iio]
            [incanter.charts :as icharts]
            [clojure.tools.logging :as log]
            [loom.graph :refer :all]
            [loom.alg :refer :all]
            [datomic-schema.schema :refer :all]
            [datomic-schema.schema :as s]
            [loom.attr :as attr])
  (:gen-class)
  (:import java.util.Date))

(def uri "datomic:mem://interiors-analytics-db")
;(def uri "datomic:sql://datomic?jdbc:postgresql://localhost:5432/datomic?user=datomic&password=datomic")
;(def uri "datomic:sql://interiors-analytics-db")

(defn testInteriors1 []
  (log/info "Reached Interiors"))

(def paints_db [{:brand "Asian" 
              :catalog 
              [
              {:productName "Tractor Acrylic Distemper"
        :type "distemper"
        :category "acrylic"
        :substrate "TAD can be applied on smooth plasters, false ceiling, asbestos sheets, concrete etc"
        :coverage "85-90"
        :finish "Matt Finish. No sheen" 
        :durability "3 Yrs"
        :usage "Sand the surface with Emery paper 100 and wipe clean. Apply Asian Paints Primer @ 8-10% dilution with water or turpentine (depends on primer used). After 6-8 hrs for drying, apply Asian Paints Wall Putty after 6-8 hrs drying sand the surface with Emery paper  and  Apply Asian Paints Primer @ 8-10%  3-4 Hrs between 2 Coats"}
        {:productName "Tractor Synthetic Distemper"
        :type "distemper"
        :category "synthetic"
        :substrate "Tractor Synthetic Distemper can be applied on various types of plasters, false ceiling, asbestos sheets, concrete etc. Designed for interiors"
        :coverage "85-118" 
        :finish "Matt Finish. No sheen"
        :durability "3-4 years"
        :usage "Sand the surface with Emery paper 180 and wipe clean. Apply Asian Paints Primer @ 8-10% dilution with water or turpentine (depends on primer used). After 6-8 hrs for drying, apply Asian Paints Wall Putty. after 4-6 hrs drying sand the surface with Emery paper 180 and wipe clean. again apply Asian Paints Primer. Then apply 2 coats of TSD @ 50-60% dilution with water. Recoating period is 6 hrs"}

        {:productName "Utsav Acrylilc Distemper"
        :type "distemper"
        :category "acrylic"
        :substrate "Utsav Acrylilc Distemper can be applied on various types of plasters, false ceiling, asbestos sheets, concrete etc. Designed for interiors"
        :coverage "75-85"
        :finish "Matt Finish. No sheen"
        :durability "3 years" 
        :usage "Sand the surface with Emery paper 100 and wipe clean. Apply Asian Paints Primer @ 8-10% dilution with water or turpentine (depends on primer used). After 6-8 hrs for drying, apply Asian Paints Wall Putty. after 6-8 hrs drying sand the surface with Emery paper 180 and wipe clean. again apply Asian Paints Primer. Then apply 2 coats of UAD @ 65-75% dilution with water. Recoating period is 3-4 hrs"}

        {:productName "Utsav Distemper"
        :type "distemper"
        :category "acrylic"
        :substrate "Utsav Distemper should be used only on interior surfaces. It can be applied on suitably prepared masonry wall surfaces"
        :coverage "60-75" 
        :finish "Matt Finish. No sheen"
        :durability "2 years"
        :usage "Sand the Surface with Emery paper 100 and wipe clean than apply 2 coats of Utsav Distemper @ 75% dilution with water. Recoating period is 4-6 hrs"}


        {:productName "Royale Play Special Effects Paint"
        :type "emulsion"
        :category "acrylic"
        :substrate "Royale Luxury Emulsion basecoat (chosen shade)"
        :coverage "100 sq ft per ltr (1 coat)"
        :finish "Soft Sheen" 
        :durability "3 years" 
        :usage "Asian Paints Decoprime ST/WT + Asian Paints Acrylic Putty + Asian Paints Decorpime ST/WT + Royale 40% dilution (2-3 coats)"}

        {:productName "Royale Play Dune"
        :type "emulsion"
        :category "acrylic" 
        :substrate "PPP, PE as base coat" 
        :coverage "45-55 sq ft per lit"
        :finish "Soft metallic sheen"
        :durability "3-4 years" 
        :usage "Asian Paints Decoprime ST/WT + Asian Paints Acrylic Putty + Asian Paints Decorpime ST/WT+ Premium Emulsion 40% (2-3 Coats)"}

        {:productName "Royale Play Safari"
        :type "emulsion"
        :category "acrylic" 
        :substrate "PPP, PE tinted as base coat" 
        :coverage "45-55 sq ft per lit"
        :finish "Soft metallic sheen"
        :durability "3-4 years"
        :usage "Asian Paints Decoprime ST/WT + Asian Paints Acrylic Putty + Asian Paints Decorpime ST/WT+ Premium Emulsion 40% (2-3 Coats)"}

        {:productName "Royale Play Metallics"
        :type "emulsion"
        :category "acrylic" 
        :substrate "Royale Luxury Emulsion basecoat (chosen shade)"
        :coverage "100 sq ft per ltr (1 coat)" 
        :finish "Soft Sheen "
        :durability "4 years"
        :usage "Asian Paints Decoprime ST/WT + Asian Paints Acrylic Putty + Asian Paints Decorpime ST/WT + Royale 40% dilution (2-3 coats)"}

        {:productName "Royale Play Stucco"
        :type "emulsion"
        :category "acrylic" 
        :substrate "PPP surface" 
        :coverage "18-20 sq ft per kg (3 coats)"
        :finish "Sheen/ Glossy Finish"
        :durability "5 years" 
        :usage "Asian Paints Decoprime ST/WT + Asian Paints Acrylic Putty + Asian Paints Decorpime ST/WT"}


        {:productName "Royale Glitter" 
        :type "emulsion"
        :category "acrylic" 
        :substrate "Royale Glitter can be applied on smooth plasters, false ceiling, asbestos sheets, concrete etc. It requires a base coat of Asian Paints Premium Emulsion White"
        :coverage "110-125" 
        :finish "Smooth metallic finish"
        :durability "5-6 years" 
        :usage "Apply Asian Paints Primer @ 8-10% dilution with water or turpentine (depends on primer used). After 6-8 hrs for drying, apply Asian Paints Wall Putty, again apply Asian Paints Primer @ 8-10% dilution after 4-6 hrs of putty application. Apply Premium Emulsion White with 60-70% dilution to get a smooth uniform white surface.Then apply 2 coats of Royale @ 40-45% dilution with water. 3-4 hrs between 2 coats"}

        {:productName "Royale luxury Emulsion"
        :type "emulsion"
        :category "acrylic" 
        :substrate "Royale can be applied on smooth plasters, false ceiling, asbestos sheets, concrete etc"
        :coverage "150-175" 
        :finish "Silky smooth with sheen"
        :durability "5-6 years" 
        :usage "Apply Asian Paints Primer @ 8-10% dilution with water or turpentine (depends on primer used). After 6-8 hrs for drying, apply Asian Paints Wall Putty, again apply Asian Paints Primer @ 8-10% dilution after 4-6 hrs of putty application. Then apply 2 coats of Royale @ 40-45% dilution with water. 3-4 hrs between 2 coats"}

        {:productName "Premium Emulsion"
        :type "emulsion" 
        :category "acrylic"
        :substrate "PE can be applied on smooth plasters, false ceiling, asbestos sheets, concrete etc"
        :coverage "150-170" 
        :finish "Matt Finish with slight sheen"
        :durability "4-5 years" 
        :usage "Apply Asian Paints Primer @ 8-10% dilution with water or turpentine (depends on primer used). After 6-8 hrs for drying, apply Asian Paints Wall Putty, again apply Asian Paints Primer @ 8-10% dilution after 4-6 hrs of putty application. Then apply 2 coats of PE @ 60-70% dilution with water. 3-4 hrs between 2 coats"}

         
        {:productName "Tractor Emulsion"
        :type "emulsion" 
        :category "acrylic"
        :substrate "TE can be applied on smooth plasters, false ceiling, asbestos sheets, concrete etc"
        :coverage "140-150" 
        :finish "Matt Finish with slight sheen"
        :durability "4-5 years" 
        :usage "Apply Asian Paints Primer @ 8-10% dilution with water or turpentine (depends on primer used). After 6-8 hrs for drying, apply Asian Paints Wall Putty, again apply Asian Paints Primer @ 8-10% dilution after 4-6 hrs of putty application. Then apply 2 coats of TE @ 50-75% dilution with water. 4 hrs between 2 coats"}

        {:productName "Interior Wall Finish - Matt"
        :type "emulsion"
        :category "acrylic" 
        :substrate "Asian Paints Matt can be applied on suitably prepared masonry walls"
        :coverage "100-130" 
        :finish "Matt" 
        :durability "3 years" 
        :usage "Apply Asian Paints Primer @ 8-10% dilution with water or turpentine (depends on primer used). After 6-8 hrs for drying, apply Asian Paints Wall Putty, again apply Asian Paints Primer @ 8-10% dilution after 4-6 hrs of putty application. Then apply 2 coats of Asian Paints Matt Finish @ 7-9% dilution with Mineral Turpentine. 8 hrs between 2 coats"}

        {:productName "Interior Wall Finish - Lustre"
        :type "emulsion" 
        :category "acrylic"
        :substrate "Asian Paints Lustre can be applied on suitably prepared masonry walls"
        :coverage "110-150" 
        :finish "Silky smooth finish" 
        :durability "4-5 years"
        :usage "Apply Asian Paints Primer @ 8-10% dilution with water or turpentine (depends on primer used). After 6-8 hrs for drying, apply Asian Paints Wall Putty, again apply Asian Paints Primer @ 8-10% dilution after 4-6 hrs of putty application. Then apply 2 coats of Asian Paints Lustre Finish @ 7-9% dilution with Mineral Turpentine. 8 hrs between 2 coats"}


        {:productName "Premium Semi Gloss Enamel"
        :type "enamel"
        :category ""
        :substrate "Premium Semi Gloss Enamel can be applied on  wood, metal surfaces and walls"
        :coverage "90-110"
        :finish "Semi Gloss" 
        :durability "3 years" 
        :usage "Apply Asian Paints Primer @ 8-10% dilution with turpentine. Premium Semi Gloss Enamel can also be used as primer @100% dilution with water.   After 6-8 hrs of drying, apply Asian Paints Wall Putty (for masonry or cement surfaces) or Asian Paints Knifing Paste Filler (for wood or metal surfaces).  Again apply Asian Paints Primer @ 8-10% dilution with turpentine or Premium Semi Gloss Enamel @ 100% dilution with water after 4-6 hrs of putty application (6-8 hrs in case Knifing Paste Filler is used).  Then apply 2 coats of Premium Semi Gloss Enamel @ 15-25% dilution with water. 4-6 hrs between 2 coats"}

        {:productName "Premium Satin Enamel"
        :type "enamel"
        :category "enamel"
        :substrate "Premium Satin Enamel can be applied on wood, metal surfaces and walls"
        :coverage "100-120" 
        :finish "Low Gloss"
        :durability "3 years" 
        :usage "Apply Asian Paints Primer @ 8-10% dilution with turpentine.  After 6-8 hrs of drying, apply Asian Paints Wall Putty (for masonry or cement surfaces) or Asian Paints Knifing Paste Filler (for wood or metal surfaces).  Again apply Asian Paints Primer @ 8-10% dilution after 4-6 hrs of putty application (6-8 hrs in case Knifing Paste Filler is used).  Then apply 2 coats of Premium Satin Enamel @ 15-20% dilution with turpentine. 6-8 hrs between 2 coats"}

        {:productName "Premium Gloss Enamel"
        :type "enamel"
        :category "enamel"
        :substrate "Premium  Gloss Enamel can be applied on  wood, metal surfaces and walls"
        :coverage "100" 
        :finish "Glossy"
        :durability "3 years"
        :usage "Apply Asian Paints Primer @ 8-10% dilution with turpentine.  After 6-8 hrs of drying, apply Asian Paints Wall Putty (for masonry or cement surfaces) or Asian Paints Knifing Paste Filler (for wood or metal surfaces).  Again apply Asian Paints Primer @ 8-10% dilution with turpentine o .  Then apply 2 coats of Premium Gloss Enamel @ 15-25% dilution with water. 4-6 hrs between 2 coats"}

        {:productName "Utsav Enamel" 
        :type "enamel"
        :category "enamel"
        :substrate "Utsav Enamel is recommended for usage on interior surfaces. It can be applied on wooden surfaces (like doors, windows, furniture, cabinets) and also on 
        metal surfaces like grills, furniture, and on suitably prepared masonry wall surfaces"
        :coverage "75-95" 
        :finish "Glossy" 
        :durability "2 years" 
        :usage "Sand the surface with Emery paper 180 and wipe clean. Apply asian paints primer @ 8-10 dilution with turpentine. After 6-8 hrs drying apply asian paints wall putty after 6-8 hrs drying sand the saruface with Emery paper 180 and Wipe clean. Again apply asian paints primer. Then apply 2 coat of Utsav Enamel @8-10% dilution with terpentine. Recoating period is 6-8 hrs"}


        {:productName "Asian Paints Apex Ultima Advanced Anti Algal Weather Proof Emulsion"
        :substrate "Exterior cement plaster, false ceilings, asbestos sheets, concrete, not to be applied on Mangalore tiles"
        :coverage "50 - 60 sq ft / ltr 3-12 units at 60 deg gloss head"
        :durability "7 years"
        :finish ""
        :type ""
        :category "enamel"
        :usage "Asian Paints Exterior Wall Primer @100% dilution.  2 coat Apex Ultima application @35-40% dilution. 4-6 hrs between coats"}

        {:productName "Asian Paints Apex Weather Proof Exterior Emulsion"
        :substrate "Exterior cement plaster, false ceilings, asbestos sheets, concrete, not to be applied on Mangalore tiles"
        :coverage "50 - 60 sq ft / ltr "
        :finish "Soft Sheen"
        :durability "5 years"
        :type ""
        :category ""
        :usage "2 coat Apex application @ 40% dilution 4 hrs between coats"}

        {:productName "Apex Duracast RoughTex"
        :substrate "Exterior cement masonary plaster"
        :coverage "3 - 3.5 sq feet / kg" 
        :finish "Matt Finish" 
        :durability "8-10 years" 
        :type ""
        :category "exterior"
        :usage "Apex Duracast Roughtex applied without dilution to get a 2-2.5 mm thick coat. Rub with trowel to get desired texture. Allow to dry overnight. Two top coats with Apex/Apex Ultima of desired shade at 40% dilution"}

        {:productName "Apex Duracast FineTex"
        :substrate "Exterior cement masonary plaster, Interior masonary surfaces"
        :coverage "15 sq feet/kg"
        :finish "Matt Finish" 
        :durability "5 - 7 years" 
        :type ""
        :category "resi_exterior"
        :usage "One coat of exterior Primer @ 100% dilution. One coat of Apex Duracast Finetex with honeycomb roller to get desired texture. Allow to dry overnight. Two top coats with Apex/Apex Ultima of desired shade at 40% dilution"}

        {:productName "Apex Duracast Dholpur Tex"
        :substrate "Sand Faced Plasters, Concrete brickwork etc. Can also applied on suitable surfaces in interiors"
        :coverage "2.5sq feet / kg" 
        :finish "Matt Finish" 
        :durability "5 - 7 years"
        :type ""
        :category "resi_exterior"
        :usage "Apex Duracast Dholpur tex  applied without dilution . Rub with trowel to get desired texture. Allow to dry overnight. Two top coats with Apex/Apex Ultima of desired shade at 40% dilution"}

        {:productName "Apex Duracast Swirltex"
        :substrate "Sand Faced Plasters, Concrete brickwork etc. Can also applied on suitable surfaces in interiors"
         :coverage "3.54sq feet / kg"
        :finish "Matt Finish"
        :durability "5 - 7 years"
        :type ""
        :category "resi_exterior"
        :usage "Apex Duracast Roughtex applied without dilution to get a 2-2.5 mm thick coat. Rub with trowel to get desired texture. Allow to dry overnight. Two top coats with Apex/Apex Ultima of desired shade at 40% dilution"}

        {:productName "Asian Paints Apex Stretch Water Repellant Exterior Paint"
        :substrate "Exterior cement plaster, false ceilings, asbestos sheets, concrete, not to be applied on Mangalore tiles"
        :coverage "50 - 60 sq ft / ltr 10-20 units at 60 deg"
        :finish "gloss head"
        :durability "7 years"
        :type ""
        :category "resi_exterior" 
        :usage "Asian Paints Exterior Sealer @ 10% dilution 2 coat Apex stretch application @ 35-40% dilution. 6 hours between coats"}

        {:productName "Asian Paints Ace Exterior Emulsion/Asian Paints Ace Supreme Exterior Emulsion"
        :substrate "Exterior cement plaster, false ceilings, asbestos sheets, concrete, not to be applied on Mangalore tiles"
        :coverage "50-60 sq ft / ltr" 
        :finish "Matt"
        :durability "3 years (non-coastal markets)"
        :type ""
        :category "resi_exterior"
        :usage "Asian Paints Exterior Sealer @ 10% dilution 2 coat ACE application @ 75% dilution. 4 hours between coats"}



        {:productName "Utsav Acrylic Distemper"
        :substrate "Utsav Acrylilc Distemper can be applied on various types of plasters, false ceiling, asbestos sheets, concrete etc. Designed for interiors"
        :coverage "2 coats 7-8" 
        :finish "Matt Finish" 
        :durability "3 years "
        :type ""
        :category "resi_interior"
        :usage "Sand the surface with Emery paper 100 and wipe clean. Apply Asian Paints Primer @ 8-10% dilution with water or turpentine (depends on primer used). After 6-8 hrs for drying, apply Asian Paints Wall Putty after 6-8 hrs drying sand the surface with Emery paper 180 and wipe clean. again apply Asian Paints Primer. Then apply 2 coats of UAD @ 65-75% dilution with water. Recoating period is 3-4 hrs"}

        {:productName "Utsav Enamel" 
        :substrate "Utsav Enamel is recommended for usage on interior surfaces. It can be applied on wooden surfaces (like doors, windows, furniture, cabinets) and also on metal surfaces like grills, furniture, and on suitably prepared masonry wall surfaces"
        :coverage "2 coats 7-9" 
        :finish "Glossy"
        :durability "2 years"
        :type ""
        :category "resi_interior"
        :usage "Sand the surface with Emery paper 180 and wipe clean. Apply asian paints primer @ 8-10 dilution with turpentine. After 6-8 hrs drying apply asian paints wall putty after 6-8 hrs drying sand the saruface with Emery paper 180 and Wipe clean. Again apply asi"}

        {:productName "Utsav Distemper" 
        :substrate "Utsav Distemper should be used only on interior surfaces. It can be applied on suitably prepared masonry wall surfaces"
        :coverage "2 coats 6-7"
        :finish "Matt Finish" 
        :durability "2 years" 
        :type ""
        :category "resi_interior"
        :usage "Sand the Surface with Emery paper 100 and wipe clean than apply 2 coats of Utsav Distemper @ 75% dilution with water. Recoating period is 4-6 hrs"}

        {:productName "Utsav Floor Colour"
        :substrate "Asian paints Utsav Flooring colour can be used to impart colour to cement surfaces" 
        :durability "10 years" 
        :coverage "1 coat 14-16" 
        :finish ""
        :type ""
        :category "resi_interior"
        :usage "Add 1 kg of Utsav Floor Colour to 6-8 kg of cement. Add suitable quantity of water to make a homogeneous free flowing paste. Clean and moisten the surface to be coated. Sprinkle dry cement on the moist surface. Allow it to set in for at least 1 hr. Apply the paste as a"} 

        {:productName "Utsav Metal Primer Red Oxide"
        :substrate "Utsav Metal primer - Red oxide is suitable for priming ferrous metal substrates"
        :coverage "1 coat 14-16" 
        :finish "Matt"
        :durability "5-6 years" 
        :type ""
        :category "resi_interior"
        :usage "Apply Asian Paints Utsav Metal Primer Red Oxide @ 4-5% dilution with turpentine. After 6-8 hrs for drying, apply AKPF (optional) allow it to dry for 4-6 hrs. Sand lightly with Emery paper 180 and wipe clean. Apply 2nd of Utsav Metal Primer Red Oxide on ar"}

        {:productName "Utsav Primer (WT)"
        :substrate "Asian paints Utsav Primer WT is suitable for priming plastered walls, asbestors, cement and concrete etc"
        :coverage "1 coat 20-25"
        :finish "Matt" 
        :durability "4-5 years" 
        :type ""
        :category "resi_interior"
        :usage "Sand the surface with Emery paper 180 and wipe clean. Apply asian paints Utsav primer WT @ 100% dilution with water. After 4-6 hrs drying apply asian paints wall putty after 6-8 hrs drying sand lightly with Emery paper 180 and Wipe clean. Again apply asia"}

        {:productName "Utsav Primer (ST)"
        :substrate "Asian paints utsva primer ST should be used only on interior surfaces. It can be applied on suitably prepared masonry and wall surfaces, as well as wooden surfaces like doors, window, furniture, cabinets etc"
        :coverage "1 coat 18-20" 
        :finish "Sheen" 
        :durability "4-5 years" 
        :type ""
        :category "resi_interior"
        :usage "Sand the surface with Emery paper 180 and wipe clean. Apply asian paints Utsav primer ST @ 8-10 dilution with turpentine. After 6-8 hrs drying apply asian paints wall putty after 6-8 hrs drying sand the sarface with Emery paper 180 and Wipe clean. Again apply asian paints Utsav primer ST."}]
        }])

(def paint-graph (weighted-digraph [:painting :commercial 10]
                           [:painting :residential 20]
                           [:commercial :comm_interior 30]
                           [:commercial :comm_exterior 10]
                           [:residential :resi_interior 30]
                           [:residential :resi_exterior 10]
                           [:comm_interior :hospital 10]
                           [:comm_interior :offices 10]
                           [:comm_interior :factory 10]
                           [:comm_interior :retail 10]
                           [:hospital :anti_bacterial 10]
                           [:factory :anti_corrosive 10]
                           [:comm_exterior :exterior_emulsion 10]
                           [:comm_exterior :exterior_cement_based 10]
                           [:comm_exterior :exterior_texture_based 10]
                           [:resi_interior :distemper 30]
                           [:resi_interior :emulsion 30]
                           [:resi_interior :washable_paints 30]
                           [:resi_interior :enamel 30]
                           [:resi_interior :decorative 30]
                           [:distemper :oil_based 30]
                           [:distemper :dry 30]
                           [:emulsion :acrylic 30]
                           [:emulsion :plastic 30]
                           [:emulsion :superior 30]
                           [:emulsion :washable 30]
                           [:resi_exterior :exterior_emulsion 10]
                           [:resi_exterior :exterior_cement_based 10]
                           [:resi_exterior :exterior_texture_based 10]
                           [:resi_interior :woodwork 10]
                           [:resi_interior :ceiling 10]
                           [:resi_interior :wall 10]
                           [:resi_interior :mild_steel 10]
                           [:wall :distemper 10]
                           [:wall :emulsion 10]
                           [:wall :decorative 10]
                           [:wall :accent_paints 10]
                           [:wall :wall_paper 10]
                           [:woodwork :melamine 10]
                           [:woodwork :hand_polish 10]
                           [:woodwork :water_based 10]
                           [:woodwork :laquer_based 10]
                           [:woodwork :poly_coat 10]
                           [:woodwork :duco 10]
                           [:ceiling :distemper 10]
                           [:ceiling :emulsion 10]
                           [:mild_steel :enamel 10]
                           [:mild_steel :satin_enamel 10]
                           [:mild_steel :flat_enamel 10]
                           [:mild_steel :auto_coat 10]
                           ))

(def paints [{:brand "Asian" 
              :catalog [{:type "emulsion" :shade "white" :category "acrylic"}
                         {:type "emulsion" :shade "red" :category "plastic"}
                         {:type "emulsion" :shade "blue" :category "superior"}
                         {:type "emulsion" :shade "grey" :category "washable"}
                         {:type "distemper" :shade "white" :category "oil_based"}
                         {:type "distemper" :shade "blue" :category "oil_based"}
                         {:type "distemper" :shade "brown" :category "dry"}
                         {:type "distemper" :shade "yellow" :category "dry"}]}
             {:brand "Berger" 
              :catalog [{:type "emulsion" :shade "white" :category "acrylic"}
                       {:type "emulsion" :shade "gold" :category "plastic"}
                       {:type "emulsion" :shade "emerald" :category "superior"}
                       {:type "emulsion" :shade "light grey" :category "washable"}
                       {:type "distemper" :shade "pearl white" :category "oil_based"}
                       {:type "distemper" :shade "dark blue" :category "oil_based"}
                       {:type "distemper" :shade "light brown" :category "dry"}
                       {:type "distemper" :shade "bright yellow" :category "dry"}]}
             {:brand "Birla" 
              :catalog [{:type "emulsion" :shade "milk white" :category "acrylic"}
                       {:type "emulsion" :shade "postoffice red" :category "plastic"}
                       {:type "emulsion" :shade "sky blue" :category "superior"}
                       {:type "emulsion" :shade "medium grey" :category "washable"}
                       {:type "distemper" :shade "bright white" :category "oil_based"}
                       {:type "distemper" :shade "turquoise blue" :category "oil_based"}
                       {:type "distemper" :shade "chocolate brown" :category "dry"}
                       {:type "distemper" :shade "pale yellow" :category "dry"}]}
            ])

(def date (java.util.Date.))

(defn listdbs []
  (log/info "EXISTING dbs" (d/get-database-names "datomic:mem://*")))

(defonce db-url "datomic:mem://testdb")

(defdbfn dbinc [db e a qty] :db.part/user
  [[:db/add e a (+ qty (or (get (d/entity db e) a) 0))]])

(defn paintparts []
  [(part "paintapp")])

(defn paintschema []
  [(schema paint
    (fields
     [brand :string :indexed]
     [type :ref :many]))

   (schema type
    (fields
     [paint_type :string]
     [shade :string]
     [productName :string]
     [substrate :string]
     [coverage :string]
     [finish :string]
     [durability :string] 
     [usage :string]
     [category :string]))])

(defn setup-paint-db [url]
  (d/create-database url)
  (d/transact
   (d/connect url)
   (concat
    (s/generate-parts (paintparts))
    (s/generate-schema (paintschema))
    (s/dbfns->datomic dbinc))))


(defn shutdown-db [dburi]
  (log/info "DELETING db " (d/delete-database dburi)))


 (defn get-tree [query]
  (dijkstra-span paint-graph (keyword query))
  (dijkstra-span paint-graph (keyword "emulsion"))
   )

 (defn get-successor [query]
  (successors paint-graph (keyword query)))

 (defn get-predecessor [query]
  (predecessors paint-graph (keyword query)))


(defn get-search-spec-old [query]
  (into [] (map #(name %) (bf-traverse paint-graph (keyword query)))))

(defn get-search-spec [query]
  (into [] (map #(name %) (successors paint-graph (keyword query)))))



(defn test-pull [param]
  (let [conn (d/connect uri)]
  (d/q '[:find [(pull ?eid [:paint/brand {:paint/type [:paint/shade]}]) ...]
         :in $ ?param
         :where [?eid :paint/brand ?brand]
                [?eid :paint/type ?type_eid]
                [?type_eid :paint/paint_type ?type]
                [?type_eid :paint/shade ?param]
                [?type_eid :paint/category ?category]

         ]
       (d/db conn)
       param)))

(defn test-pull-2 [param]
  (let [conn (d/connect uri)]
  (d/q '[:find [(pull ?type_eid [:paint/paint_type :paint/shade :paint/category])]
         :in $ ?param
         :where [?eid :paint/brand ?brand]
                [?eid :paint/type ?type_eid]
                [?type_eid :paint/paint_type ?type]
                [?type_eid :paint/shade ?param]
                [?type_eid :paint/category ?category]

         ]
       (d/db conn)
       param)))


(defn format-paint-entry [elem]
    {:db/id (d/tempid :db.part/user)
     :type/paint_type (:type elem)
     :type/productName (:productName elem)
     :type/substrate (:substrate elem)
     :type/coverage (:coverage elem)
     :type/finish (:finish elem)
     :type/durability (:durability elem) 
     :type/usage (:usage elem)
     :type/category (:category elem)})

(defn add-paint-db [idx]
  (let [conn (d/connect db-url)]
  (log/info [{:db/id (d/tempid :db.part/user)
                      :paint/brand (:brand (get paints_db idx))
                      :paint/type (into [] (map format-paint-entry (:catalog (get paints_db idx))))
                      }
                      ])

  @(d/transact conn [{:db/id (d/tempid :db.part/user)
                      :paint/brand (:brand (get paints_db idx))
                      :paint/type (into [] (map format-paint-entry (:catalog (get paints_db idx))))
                      }])
  
  ))


(defn query-paints []
  (let [conn (d/connect db-url)]
  (d/q '[:find ?brand ?type ?category ?usage ?durability ?finish ?substrate ?productName
         :in $
         :where [?eid :paint/brand ?brand]
                [?eid :paint/type ?type_eid]
                [?type_eid :type/paint_type ?type]
                [?type_eid :type/usage ?usage]
                [?type_eid :type/durability ?durability]
                [?type_eid :type/finish ?finish]
                [?type_eid :type/substrate ?substrate]
                [?type_eid :type/productName ?productName]
                [?type_eid :type/category ?category]

         ]
       (d/db conn))))


(defn query-paint-by-category [category]
  (let [conn (d/connect db-url)]
  (d/q '[:find ?brand ?type ?category
         :in $ ?category
         :where [?eid :paint/brand ?brand]
                [?eid :paint/type ?type_eid]
                [?type_eid :type/paint_type ?type]
                [?type_eid :type/category ?category]]
       (d/db conn)
       category)))


(defn query-paint [query-str]
  (let [conn (d/connect db-url)]
  (d/q '[:find ?brand ?type ?category ?usage ?durability ?finish ?substrate ?productName
         :in $ [?category ...]
         :where [?eid :paint/brand ?brand]
                [?eid :paint/type ?type_eid]
                [?type_eid :type/paint_type ?type]
                [?type_eid :type/usage ?usage]
                [?type_eid :type/durability ?durability]
                [?type_eid :type/finish ?finish]
                [?type_eid :type/substrate ?substrate]
                [?type_eid :type/productName ?productName]
                [?type_eid :type/category ?category]]
       (d/db conn)
       (get-search-spec query-str))))


(defn dbsetup []
  (listdbs)
  (log/info "CREATE DB")
  (setup-paint-db  db-url)
  (listdbs))

(defn dbpump []
      (do
        (add-paint-db 0)
       ; (add-paint-db 1)
       ; (add-paint-db 2)
        (listdbs)))

(defn dbload []
  (log/info "LOAD DB")
  (dbpump)
  (listdbs))

(defn dbshutdown []
  (listdbs)
  (log/info "SHUTDOWN DB")
  (shutdown-db db-url)
  (listdbs))

(defn dblist []
  (listdbs))






