from django.urls import path
from . import views

urlpatterns = [
    path('restaurant', views.getRestaurant, name="getRestaurant"),
    path('restaurant/<int:restaurant>/menu', views.getMenuByRestaurant, name="getMenuByRestaurant"),
    path('restaurant/<int:restaurant>/review', views.getReviewByRestaurant, name="getReviewByRestaurant"),
    path('menu/<int:menu>/nutrition', views.getNutritionByMenu, name="getNutritionByMenu"),
    path('allergy', views.UserAllergyView, name="UserAllergyView"),
    path('review', views.postReview), # 리뷰 등록 url
    path('review/<int:id>', views.getReview), # id에 해당하는 인덱스를 가진 리뷰를 조회 url
    path('review/user', views.getUserReview), # 사용자가 작성한 리뷰 조회 url
    path('review/delete/<int:id>', views.deleteReview), # 리뷰 삭제 url
    path('preference', views.getMenuPreference),
    path('preference/update', views.postMenuPreference),
]
