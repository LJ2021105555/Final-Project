
# web
pip install django djangorestframework djangorestframework-simplejwt pillow

python manage.py makemigrations
python manage.py migrate


#创建超级用户
python manage.py createsuperuser
#创建普通用户
python manage.py shell
from django.contrib.auth.models import User
User.objects.create_user(username='test', 'test@example.com',password='123456')