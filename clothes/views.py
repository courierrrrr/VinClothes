from rest_framework import generics, status, permissions, filters
from rest_framework.decorators import api_view, permission_classes
from rest_framework.response import Response
from django.contrib.auth.models import User
from django_filters.rest_framework import DjangoFilterBackend
from .models import ClothingItem, Rating
from .serializers import ClothingItemSerializer, UserSerializer
from .permissions import IsOwnerOrReadOnly
from rest_framework.authtoken.models import Token
from django.contrib.auth import authenticate


@api_view(['POST'])
@permission_classes([permissions.AllowAny])
def login(request):
    username = request.data.get('username')
    password = request.data.get('password')

    user = authenticate(username=username, password=password)

    if user is None:
        return Response(
            {'error': 'Invalid credentials'},
            status=status.HTTP_401_UNAUTHORIZED
        )

    token, _ = Token.objects.get_or_create(user=user)
    return Response({'token': token.key})

class ClothingItemList(generics.ListCreateAPIView):
    """
    Returns a list of all clothing items.
    """
    queryset = ClothingItem.objects.all()
    serializer_class = ClothingItemSerializer
    filter_backends = [DjangoFilterBackend, filters.SearchFilter, filters.OrderingFilter]
    search_fields = ['title', 'description', 'brand']
    ordering_fields = ['title', 'decade', 'brand', 'size', 'clothing_type']
    filterset_fields = ['clothing_type', 'decade', 'size', 'brand', 'owner']
    permission_classes = [permissions.IsAuthenticatedOrReadOnly, IsOwnerOrReadOnly]

    def perform_create(self, serializer):
        serializer.save(owner=self.request.user)


class ClothingItemDetail(generics.RetrieveUpdateDestroyAPIView):
    """
    Retrieve, update or delete a clothing item.
    """
    queryset = ClothingItem.objects.all()
    serializer_class = ClothingItemSerializer
    permission_classes = [permissions.IsAuthenticatedOrReadOnly, IsOwnerOrReadOnly]


@api_view(['POST'])
@permission_classes([permissions.IsAuthenticatedOrReadOnly])
def rate_clothing(request, pk):
    """
    Submit a star rating for a clothing item.
    """
    try:
        item = ClothingItem.objects.get(pk=pk)
    except ClothingItem.DoesNotExist:
        return Response(status=status.HTTP_404_NOT_FOUND)

    stars = request.data.get('stars')

    if stars is None:
        return Response(
            {'error': 'Stars are required'},
            status=status.HTTP_400_BAD_REQUEST
        )

    stars = int(stars)

    if stars < 1 or stars > 5:
        return Response(
            {'error': 'Stars must be between 1 and 5'},
            status=status.HTTP_400_BAD_REQUEST
        )

    Rating.objects.create(clothing_item=item, stars=stars)

    return Response(ClothingItemSerializer(item).data)