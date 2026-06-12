from django.db import models


class ClothingItem(models.Model):
    created = models.DateTimeField(auto_now_add=True)
    title = models.CharField(max_length=200)
    brand = models.CharField(max_length=200)
    size = models.CharField(max_length=50)
    decade = models.CharField(max_length=50)
    clothing_type = models.CharField(max_length=100)
    description = models.TextField(blank=True)
    video_url = models.URLField(blank=True)
    image = models.BinaryField(blank=True, null=True)
    owner = models.ForeignKey('auth.User', related_name='clothes', on_delete=models.CASCADE, null=True)

    def average_rating(self):
        ratings = self.ratings.all()
        if ratings:
            return sum(r.stars for r in ratings) / len(ratings)
        return 0

    def __str__(self):
        return self.title


class Rating(models.Model):
    clothing_item = models.ForeignKey(
        ClothingItem,
        on_delete=models.CASCADE,
        related_name='ratings'
    )
    stars = models.IntegerField()

    def __str__(self):
        return f"{self.clothing_item.title} - {self.stars} stars"