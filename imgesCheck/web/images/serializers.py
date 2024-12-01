from rest_framework import serializers
from .models import Image

class ImageSerializer(serializers.ModelSerializer):
    check_json = serializers.JSONField(required=False, allow_null=True)
    name=serializers.JSONField(required=False, allow_null=True)
    class Meta:
        model = Image
        fields = ['image','name','check_json']

    def create(self, validated_data):
        image_instance = Image.objects.create(**validated_data)
        return image_instance

