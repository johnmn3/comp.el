(ns todomvc.views
  (:require
   [comp.el :as comp]
   [todomvc.views.comps :as c]
   [todomvc.views.styled :as styled]
   [reagent.core :as reagent]
   [re-frame.core :refer [dispatch subscribe]]))

(defn new-todo-box []
  (let [all-complete? @(subscribe [:all-complete?])]
    [comp/container styled/new-todo-styles
     [comp/item {:xs 1
                 :on-click #(dispatch [:complete-all-toggle])}
      [comp/arrow-down {:font-size "large"
                        :style (merge {:padding-top 3
                                       :padding-left 0
                                       :color        "#e6e6e6"}
                                      (when all-complete?
                                        {:color "#737373"}))}]]
     [comp/item {:xs 11}
      [c/new-todo]]]))

(defn todo-item []
  (let [editing (reagent/atom false)
        hover-state (reagent/atom false)]
    (fn [{{:as todo :keys [id title]} :todo}]
      [comp/list-item
       (merge styled/todo-item
              {:on-mouse-over #(reset! hover-state true)
               :on-mouse-out #(reset! hover-state false)})
       [c/complete-check todo]
       (if-not @editing
         [c/todo-display (assoc todo :editing editing)
          title]
         [(c/existing-todo
           {:as ::edit-existing-todo
            :props {:todo todo
                    :editing editing}})])
       (when @hover-state
         [c/delete-todo {:is id}])])))

(def todo-list
  (comp/list-items
   {:as ::todo-list}
   #(let [visible-todos @(subscribe [:visible-todos])]
      (->> visible-todos
           (mapv (fn [x] [todo-item {:todo x}]))
           (interpose [comp/divider])
           (into [[comp/divider]])))))

(defn footer-selectors []
  (let [showing @(subscribe [:showing])]
    [comp/container
     [comp/item {:xs 3}
      [c/filter-all {:selected? showing}]]
     [comp/item {:xs 5}
      [c/filter-active {:selected? showing}]]
     [comp/item {:xs 4}
      [c/filter-done {:selected? showing}]]]))

(defn footer-controls []
  (let [[active done] @(subscribe [:footer-counts])]
    [comp/paper styled/footer-controls
     [comp/container
      [comp/item {:xs 2}
       active " " (case active 1 "item" "items") " left"]
      [comp/item {:xs 2}]
      [comp/item {:xs 3 :container true}
       [footer-selectors]]
      [comp/item {:xs 2}]
      [comp/item {:xs 3
                  :on-click #(dispatch [:clear-completed])
                  :style/hover {:text-decoration "underline"
                                :cursor "pointer"}}
       (when (pos? done)
         "Clear completed")]]]))

(defn todo-app []
  [:div {:style styled/body-style}
   [comp/box {:style {:padding-top 50}}
    [c/todo-header-title "todos"]
    [comp/paper {:style {:padding-top 16
                         :box-shadow (str "0 2px 4px 0 rgba(0, 0, 0, 0.2),"
                                          "0 25px 50px 0 rgba(0, 0, 0, 0.1)")}}
     [new-todo-box]
     (when (seq @(subscribe [:todos]))
       [todo-list])
     [footer-controls]]
    [:footer styled/todo-app-footer
     [:p "Double-click to edit a todo"]]
    [comp/box {:style {:height 100}}]]])
