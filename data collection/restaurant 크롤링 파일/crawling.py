import requests
import pandas as pd

# API 호출을 위한 URL 생성

# v2 : 카카오 api 버전, &x, &y = 위도, 경도, radius = 1000 = 1km 내로 설정하겠다
api_url = 'https://dapi.kakao.com/v2/local/search/category.json?category_group_code=FD6&x=126.633642&y=37.373446&radius=1000'
# 카카오앱키에는 'KakaoAK 3811142b2c325524' 이런 형태로 넣기
headers = {'Authorization': '카카오앱키'}


response = requests.get(api_url, headers=headers)
print(response.json())
data = response.json()['documents']
restaurant_data = []
for item in data:
    name = item['place_name']
    address = item['address_name']
    phone_number = item.get('phone', '')
    category_name = item.get('category_name', '')
    restaurant_id = item.get('id', '')
    place_url = item.get('place_url', '')

    restaurant_data.append([name, address, phone_number, category_name, restaurant_id, place_url])

# DataFrame 생성하기
columns = ['name', 'address', 'phone_number', 'category_name', 'restaurant_id', 'place_url']
df = pd.DataFrame(restaurant_data, columns=columns)

# CSV 파일로 저장하기
df.to_csv('restaurant_data4.csv', index=True)