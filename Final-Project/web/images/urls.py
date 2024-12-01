from django.urls import path
from .views import ImageListView
from . import views

urlpatterns = [
    path('images/', ImageListView.as_view(), name='image-list'),
]
