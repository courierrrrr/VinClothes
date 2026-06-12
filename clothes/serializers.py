from rest_framework import serializers
from .models import ClothingItem, Rating
from django.contrib.auth.models import User
import base64


class RatingSerializer(serializers.ModelSerializer):
    class Meta:
        model = Rating
        fields = ['id', 'stars']


class ClothingItemSerializer(serializers.ModelSerializer):
    average_rating = serializers.SerializerMethodField()
    ratings = RatingSerializer(many=True, read_only=True)
    image = serializers.CharField(required=False, allow_null=True)

    class Meta:
        model = ClothingItem
        fields = [
            'id', 'title', 'brand', 'size', 'decade',
            'clothing_type', 'description', 'video_url',
            'average_rating', 'ratings', 'image', 'owner'
        ]

    def get_average_rating(self, obj):
        return obj.average_rating()

    def to_representation(self, instance):
        rep = super().to_representation(instance)
        if instance.image:
            rep['image'] = base64.b64encode(bytes(instance.image)).decode('utf-8')
        else:
            rep['image'] = None
        return rep

    def create(self, validated_data):
        image_base64 = validated_data.pop('image', None)
        item = ClothingItem(**validated_data)
        if image_base64:
            item.image = base64.b64decode(image_base64)
        item.save()
        return item

    def update(self, instance, validated_data):
        image_base64 = validated_data.pop('image', None)
        for attr, value in validated_data.items():
            setattr(instance, attr, value)
        if image_base64:
            instance.image = base64.b64decode(image_base64)
        instance.save()
        return instance


class UserSerializer(serializers.ModelSerializer):
    clothes = serializers.PrimaryKeyRelatedField(many=True, queryset=ClothingItem.objects.all())

    class Meta:
        model = User
        fields = ['id', 'username', 'clothes']