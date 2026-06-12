from django.urls import path, include
from rest_framework.urlpatterns import format_suffix_patterns
from rest_framework.authtoken.views import obtain_auth_token
from . import views

urlpatterns = [
    path('clothes/', views.ClothingItemList.as_view()),
    path('clothes/<int:pk>/', views.ClothingItemDetail.as_view()),
    path('clothes/<int:pk>/rate/', views.rate_clothing),
    path('login/', obtain_auth_token),
]
urlpatterns += [
    path('api-auth/', include('rest_framework.urls')),
]
urlpatterns = format_suffix_patterns(urlpatterns)