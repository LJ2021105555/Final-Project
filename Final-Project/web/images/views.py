from django.shortcuts import render

from rest_framework.views import APIView
from rest_framework.response import Response
from .models import Image
from .serializers import ImageSerializer
from rest_framework import status
from rest_framework.pagination import PageNumberPagination
from rest_framework.permissions import IsAuthenticated
import requests

class ImageListView(APIView):
    permission_classes = [IsAuthenticated]
    
   

    def get(self, request):
        paginator = PageNumberPagination()
        paginator.page_size = 1000
        images = Image.objects.all().order_by('-id')
        result_page = paginator.paginate_queryset(images, request)
        serializer = ImageSerializer(result_page, many=True)
        return paginator.get_paginated_response(serializer.data)

    def post(self, request):
        image_file = request.FILES.get('image')
        if not image_file:
            return Response({"error": "No image uploaded"}, status=status.HTTP_400_BAD_REQUEST)
        serializer = ImageSerializer(data=request.data,context={'request': request})
        
        if serializer.is_valid():
             #edge url
            url = "http://127.0.0.1:5000/detect"
            response = requests.post(url, files={'image': image_file},timeout=30)
            if response.status_code == 200:
                if 'class' in response.json()[0]:
                    check_json = response.json()
                    serializer.validated_data['check_json'] = check_json
                else:
                    return Response({"error": "Invalid response from external API"}, status=status.HTTP_400_BAD_REQUEST)

                serializer.save()
                return Response({"image": serializer.data['image'],'data':serializer.data})
            else:
                return Response({"error": "Failed to call external API"}, status=status.HTTP_400_BAD_REQUEST)
        else:
            return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)