(ns todomvc.views.comps
  (:require
   [re-frame.core :refer [dispatch subscribe]]
   [clojure.string :as str]
   [reagent.core :as r]
   [comp.el :as comp]
   [todomvc.views.affects :as a]
   [todomvc.views.styled :as styled]))

(def unchecked
  (comp/span
   {:as ::unchecked :with styled/circle}))

(def checked
  (comp/span
   {:as ::checked :with styled/checked}
   #%[[comp/div styled/check-leg]
      [comp/div styled/check-foot]]))

(def complete-check
  (comp/el
   {:as ::complete-check :with a/void-todo}
   #%(let [toggle #%%(dispatch [:toggle-done %:props:id])]
       [(if %:props:done
          [checked {:on-click toggle}]
          [unchecked {:on-click toggle}])])))

(def toggle-all-down-arrow
  (comp/item
   {:as ::toggle-all-down-arrow
    :props {:xs 1
            :on-click #%(dispatch [:complete-all-toggle])}}
   #%(let [all-complete? @(subscribe [:all-complete?])]
       [[comp/arrow-down {:font-size "large"
                          :style (merge {:padding-top  3
                                         :padding-left 0
                                         :color        "#e6e6e6"}
                                        (when all-complete?
                                          {:color "#737373"}))}]])))

(def delete-todo
  (comp/div
   {:as ::delete-todo :with [styled/delete-todo a/void-todo]
    :on-click #%(dispatch [:delete-todo %:is])}
   "×"))

(def todo-display
  (comp/label
   {:as ::todo-display :with [styled/todo-display a/void-todo]
    :props/ef #%(merge {:on-double-click #%%(reset! %:editing true)}
                       (when %:done
                         styled/todo-done))}))

(def todo-input
  (comp/raw-input
   {:as ::todo-input :with [styled/todo-input a/void-todo]
    :props/void :af-state
    :props/ef #%(let [stop #%%(do (reset! %:af-state "")
                                  (when %:on-stop (%:on-stop)))
                      save #%%(do (%:on-save (some-> %:af-state deref str str/trim))
                                  (stop))]
                  {:auto-focus  true
                   :on-blur     save
                   :value       (some-> %:af-state deref)
                   :on-change   #%%(reset! %:af-state (-> %% .-target .-value))
                   :on-key-down #%%(case (.-which %%)
                                     13 (save)
                                     27 (stop)
                                     nil)})}))

(def new-todo
  (todo-input
   {:as ::new-todo :with styled/new-todo
    :props {:placeholder "What needs to be done?"
            :af-state (r/atom nil)
            :on-save #%(when (seq %)
                         (dispatch [:add-todo %]))}}))

(def existing-todo
  (todo-input
   {:as ::existing-todo :with styled/edit-todo
    :props/af #%{:af-state (r/atom %:todo:title)
                 :on-save #%%(if (seq %%)
                               (dispatch [:save %:todo:id %%])
                               (dispatch [:delete-todo %:todo:id]))
                 :on-stop #%%(reset! %:editing false)}}))

(def todo-header-title
  (comp/box
   {:as ::todo-header-title :with styled/todo-header-title}))

(def filter-anchor
  (comp/a
   {:as ::a :with [styled/filter-anchor a/selected? a/void-todo]
    :props/ef #%{:href (str "#/" %2:is%name)}
    :props {:on-selected #%(update % :style
                                   assoc :border-color
                                   "rgba(175, 47, 47, 0.2)")}}))

(def filter-all
  (filter-anchor
   {:as :all}
   "All"))

(def filter-active
  (filter-anchor
   {:as :active}
   "Active"))

(def filter-done
  (filter-anchor
   {:as :done}
   "Completed"))

(def footer-selectors
  (comp/container
   {:as :footer-selectors}
   #%(let [showing @(subscribe [:showing])]
       [[comp/item {:xs 3}
         [filter-all {:selected? showing}]]
        [comp/item {:xs 5}
         [filter-active {:selected? showing}]]
        [comp/item {:xs 4}
         [filter-done {:selected? showing}]]])))

(def footer-controls
  (comp/paper
   {:as ::footer-controls :with styled/footer-controls}
   #%(let [[active done] @(subscribe [:footer-counts])]
       [[comp/container
         [comp/item {:xs 2}
          active " " (case active 1 "item" "items") " left"]
         [comp/item {:xs 2}]
         [comp/item {:xs 3 :container true}
          [footer-selectors]]
         [comp/item {:xs 2}]
         [comp/item {:xs 3
                     :on-click #%%(dispatch [:clear-completed])
                     :style/hover {:text-decoration "underline"
                                   :cursor "pointer"}}
          (when (pos? done)
            "Clear completed")]]])))
