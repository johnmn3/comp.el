(ns comp.props
  (:require
   [af.fect :as af]))

(def void
  (af/fect
   {:as ::void
    :ef-end (fn [{:keys [props]
                  props-void :props/void}]
              {:props (apply dissoc props (af/muff props-void))})}))

(defn merge-with-styles
  [{:as parent-props parent-style :style}
   {:as child-props child-style :style}]
  (merge parent-props
         child-props
         (when (and parent-style child-style)
           {:style (merge parent-style child-style)})))

(defn props-joiner
  [{parent-props :props
    parent-props-void :props/void
    parent-props-af :props/af
    parent-props-ef :props/ef}
   {child-props :props
    child-props-void :props/void
    child-props-af :props/af
    child-props-ef :props/ef}]
  (merge {:props (merge-with-styles parent-props child-props)}
         (when (and parent-props-void child-props-void)
           {:props/void (vec (set (concat parent-props-void child-props-void)))})
         (when (and parent-props-af child-props-af)
           {:props/af (comp child-props-af parent-props-af)})
         (when (and parent-props-ef child-props-ef)
           {:props/ef (comp child-props-ef parent-props-ef)})))

(def props
  (void
   {:as ::props
    :join props-joiner
    :af (fn [{:as env
              :keys [props]
              props-af :props/af}]
          (when props-af
            {:props (merge-with-styles
                     props
                     (props-af props env))}))
    :ef (fn [{:as env
              :keys [props args]
              props-ef :props/ef
              :or {props-ef identity}}]
          (let [prop-args? (-> args first map?)
                merged-props (merge-with-styles props (when prop-args?
                                                        (first args)))]
            (if-not props-ef
              {:props merged-props}
              (let [final-props (merge {:props (merge-with-styles merged-props (props-ef merged-props env))}
                                       (when prop-args?
                                         {:args (rest args)}))]
                final-props))))}))
