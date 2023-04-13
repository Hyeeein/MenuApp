from django.shortcuts import render
from rest_framework.response import Response
from rest_framework import status
from rest_framework.response import Response
from rest_framework.decorators import api_view, permission_classes
from rest_framework.permissions import AllowAny, IsAuthenticated
from django.db.models import Max

from jmc.models import *

import numpy as np
import pandas as pd
import random

from sklearn.feature_extraction import text
from sklearn.metrics.pairwise import cosine_similarity

import warnings
warnings.filterwarnings('ignore')

# 데이터 불러오기

menu = pd.read_csv('../data/menu.csv', encoding='utf-8')
nutrient_menu = pd.read_csv('../data/nutrient.csv', encoding='cp949')
restaurant = pd.read_csv('../data/restaurant.csv', encoding='cp949')


# 추천시스템 함수 작성
@api_view(['GET'])
@permission_classes([IsAuthenticated])
def rcm(request):

    # user_id GET
    user_id = request.session.get('user_id')

    ### [기능 1] 사용자 정보에서 알러지 해당 메뉴 제외하고 추천
    user_allergy = UserAllergy.objects.filter(user_id=user_id).values()

    allergy_list = list(user_allergy) # 알러리 종류
    personal_allergy_list = []        # 유저별 알러지 임시로 담아둘 리스트

    # 알러지에 해당하는 음식 리스트 추가
    for i in range(len(allergy_list)):
        if user_allergy.iloc[user_id][i] == '1':
            personal_allergy_list.append(allergy_list[i])
    
    # nutrient 파일에서 해당 알러지가 없는 메뉴 리스트업
    not_allergy_menu = []

    for i in range(len(nutrient_menu)):
        allergy = str(nutrient_menu.iloc[i]['allergy'])
        allergy_list = allergy.split(',0')

        # 해당 알러지가 있는 메뉴를 제외한 메뉴 인덱스 리스트업
        add_menu = ''

        for j in allergy_list:
            if j not in personal_allergy_list:
                add_menu = 1
        
        if add_menu == 1:
            not_allergy_menu.append(i)
    
    # 알러지 없는 메뉴 중 한 가지 랜덤 선택 후 추천
    choice = random.randrange(0, len(not_allergy_menu))
    personal_menu = menu.iloc[not_allergy_menu[choice]]

<<<<<<< HEAD
    return personal_menu
=======
    return Response(personal_menu)
>>>>>>> ac63119acbf6aed7fddcc642f04daabeb4818e7e
