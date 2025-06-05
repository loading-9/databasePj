# databasePj
database final project
 以下是对车辆维修管理系统的 **ER 图设计** 和 **核心实体类设计** 的详细解析，基于任务需求，涵盖用户、维修人员、管理员的功能需求及数据分析需求。设计将以清晰的逻辑和结构化方式呈现，并确保数据库设计满足功能需求和数据一致性要求。

---

### ER 图设计解析

ER 图（实体-关系图）用于描述系统中的核心实体及其关系，确保数据结构清晰并支持功能实现。以下是基于任务需求的 ER 图设计分析：

#### 核心实体
根据系统需求，识别以下核心实体：
1. 用户 (User)：存储客户信息，包含注册信息、联系方式等。
2. 车辆 (Vehicle)：存储车辆信息，与用户关联。
3. 维修工单 (WorkOrder)**：记录维修任务，关联用户、车辆和维修人员。
4. **维修人员 (Technician)**：存储维修人员信息，包括工种和时薪。
5. **材料 (Material)**：记录维修过程中使用的材料及其价格。
6. **维修记录 (RepairRecord)**：存储历史维修记录，关联工单。
7. **反馈 (Feedback)**：记录用户对维修结果的反馈，如评分和评论。

#### 实体属性
1. **用户 (User)**：
   - 用户ID (UserID, 主键)
   - 用户名 (Username)
   - 密码 (Password, 加密存储)
   - 姓名 (Name)
   - 联系方式 (ContactInfo)
   - 注册时间 (RegisterTime)

2. **车辆 (Vehicle)**：
   - 车辆ID (VehicleID, 主键)
   - 用户ID (UserID, 外键，关联 User)
   - 车牌号 (LicensePlate)
   - 车型 (VehicleType)
   - 品牌 (Brand)

3. **维修工单 (WorkOrder)**：
   - 工单ID (WorkOrderID, 主键)
   - 用户ID (UserID, 外键，关联 User)
   - 车辆ID (VehicleID, 外键，关联 Vehicle)
   - 维修人员ID (TechnicianID, 外键，关联 Technician，可为空，表示未分配)
   - 工单状态 (Status, 如“待分配”、“进行中”、“已完成”)
   - 提交时间 (SubmitTime)
   - 完成时间 (CompleteTime, 可为空)
   - 工时费 (LaborCost)
   - 材料费 (MaterialCost)
   - 总费用 (TotalCost)

   -维修进度(progress， double值)

4. **维修人员 (Technician)**：
   - 维修人员ID (TechnicianID, 主键)
   - 用户名 (Username)
   - 密码 (Password, 加密存储)
   - 姓名 (Name)
   - 工种 (JobType, 如“漆工”、“焊工”、“机修”)
   - 时薪 (HourlyRate)
   - 联系方式 (ContactInfo)
   
   -工时费收入(income）

5. **材料 (Material)**：
   - 材料ID (MaterialID, 主键)
   - 工单ID (WorkOrderID, 外键，关联 WorkOrder)
   - 材料名称 (MaterialName)
   - 数量 (Quantity)
   - 单价 (UnitPrice)
   - 总价 (TotalPrice)

6. **维修记录 (RepairRecord)**：
   - 记录ID (RecordID, 主键)
   - 工单ID (WorkOrderID, 外键，关联 WorkOrder)
   - 维修人员ID (TechnicianID, 外键，关联 Technician)
   - 维修描述 (Description)
   - 维修时间 (RepairTime)

7. **反馈 (Feedback)**：
   - 反馈ID (FeedbackID, 主键)
   - 工单ID (WorkOrderID, 外键，关联 WorkOrder)
   - 用户ID (UserID, 外键，关联 User)
   - 评分 (Rating)
   - 评论 (Comment)
   - 反馈时间 (FeedbackTime)



#### ER 图描述
ER 图包含上述实体及其属性，关系通过外键和中间表体现。以下是简化的文本描述（实际 ER 图需用工具如 MySQL Workbench 或 Lucidchart 绘制）：
```
[User] --1:N--> [Vehicle]
[User] --1:N--> [WorkOrder]
[Vehicle] --1:N--> [WorkOrder]
[WorkOrder] --N:1--> [Technician] (通过中间表 WorkOrderTechnician)
[WorkOrder] --1:N--> [Material]
[WorkOrder] --1:1--> [RepairRecord]
[WorkOrder] --1:1--> [Feedback]
```


### 接口设计

以下是 RESTful API 接口设计，分为用户、维修人员和管理员功能模块。每个接口包括路径、HTTP 方法、请求参数、响应格式和说明。

统一错误response：
{
  "code": 400,
  "message": "xxxxx",
  "data": null
}

#### 用户功能接口
1. **用户注册**
   - **路径**: `POST /api/user/register`
   - **请求参数**:
     ```json
     {
       "username": "string",
       "password": "string",
       "name": "string",
       "contactInfo": "string"
     }
     ```
Response:
    {
  "code": 200,
  "message": "注册成功!",
  "data": {
    "username": "loading2",
    "name": "sy",
    "id": 4,
    "contactInfo": "137",
    "registerTime": "2025-06-02T23:43:21.1409664"
  }
}
   - **说明**: 创建新用户，密码无需加密存储。

2. **用户登录**
   - **路径**: `POST /api/user/login`
   - **请求参数**:
     ```json
     {
       "username": "string",
       "password": "string"
     }
     ```
   - **响应**:
     {
  "code": 200,
  "message": "登录成功!",
  "data": null
}

3.提交汽车
POST http://localhost:3000/api/user/{userId}/submitVehicle

请求参数
{
  "licencePlate": "12345",
  "vehicleType" : "suv",
  "brand" : "bmw",
  "manufactureYear" : 2000
 }

Response:
{
  "code": 200,
  "message": "车辆添加成功",
  "data": {
    "vehicleId": 4,
    "licencePlate": "22345",
    "vehicleType": "suv",
    "brand": "benz",
    "manufactureYear": 2000
    "userId": 1
  }
}

3. **提交维修申请**
   - **路径**: `POST  http://localhost:3000/api/user/{userId}/submitWorkOrder
   - **请求参数**:
      {
   	"vehicleId": 2,
   	"problem": "掉漆"
         “jobType": 枚举型，目前选项：漆工, 焊工, 机修
 	}

     ```
   - **响应**:
    {
  "code": 200,
  "message": "工单提交成功",
  "data": {
    "workOrderId": 2,
    "vehicleResponse": {
      "vehicleId": 4,
      "licencePlate": "22345",
      "vehicleType": "suv",
      "brand": "benz",
      "manufactureYear": 2000
    },
    "workOrderStatus": "待分配",
    "technicianDto": null,
    "problem": "掉漆",
    "submitTime": "2025-06-03T00:44:36.9942743",
    "progress": 0.0,
    "progressDescription": null
  }
}
   - **说明**: 用户提交维修申请，生成工单，状态为“待分配”。

4. **查询用户信息**
   - **路径**: `GET /api/users/{userId}`
   - **请求参数**: 无（路径参数 `userId`）
   - **响应**:
     {
  "code": 200,
  "message": "查看成功",
  "data": {
    "username": "loading1",
    "name": "sy",
    "id": 1,
    "contactInfo": "137",
    "registerTime": "2025-05-31T21:04:40"
  }
}
       ```

5.查询用户车辆
路径 GET http://localhost:3000/api/user/{userId}/vehicles
Response:
{
  "code": 200,
  "message": "查看成功",
  "data": [
    {
      "vehicleId": 1,
      "licencePlate": "12345",
      "vehicleType": "suv",
      "brand": "bmw",
      "manufactureYear": 2000
    },
    {
      "vehicleId": 4,
      "licencePlate": "22345",
      "vehicleType": "suv",
      "brand": "benz",
      "manufactureYear": 2000
    }
  ]
}


5. **查询维修工单**
   - **路径**: `GET /api/user/{userId}/work-orders`
   - **请求参数**: 无（路径参数 `userId`）
   - **响应**:
     - 200 OK:
       ```json
{
  "code": 200,
  "message": "工单查看成功",
  "data": [
    {
      "workOrderId": 1,
      "vehicleResponse": {
        "vehicleId": 1,
        "licencePlate": "12345",
        "vehicleType": "suv",
        "brand": "benz",
        "manufactureYear": 2000,
        "userId": 1
      },
      "workOrderStatus": "已完成",
      "technicianDto": {
        "technicianId": 1,
        "username": "loading1",
        "name": "sy",
        "jobType": "漆工",
        "hourlyRate": 50.0,
        "contactInfo": "137",
        "income": 50.0
      },
      "problem": "掉漆",
      "submitTime": "2025-06-04T23:18:53",
      "progress": 1.0,
      "progressDescription": "做完了"
      "laborCost": 50.0,
      "materialCost": 1.8,
      "totalCost": 51.8
    }
  ]
}
       ```
     - 404 Not Found: `{ "error": "用户不存在" }`
   - **说明**: 查询用户的所有工单信息。

6. **提交反馈**
   - **路径**: `POST http://localhost:3000/api/user/work-orders/{workOrderId}/feedback
   - **请求参数**:
 {
  "feedbackId": 任意,
  "workOrderId": 1, --对应workOrderId
  "userId": 1,
  "rating": 5,
  "comment": "还可以",
  "feedbackTime": "" null
}

   - **响应**:
{
  "code": 201,
  "message": "反馈提交成功",
  "data": {
    "feedbackId": 0,
    "workOrderId": 1,
    "userId": 1,
    "rating": 5,
    "comment": "还可以",
    "feedbackTime": null
  }
}
     


   - **说明**: 用户对已完成的工单提交评分和评论。

7.查询历史维修记录
GET http://localhost:3000/api/user/{{userId}}/repair-records
注:feedback字段可能为null,表示没有用户反馈
该接口相当于展示已完成的工单，并直接展开所有反馈
Response:
{
  "code": 200,
  "message": "维修记录查询成功",
  "data": [
    {
      "recordId": 1,
      "workOrder": {
        "workOrderId": 1,
        "vehicleResponse": {
          "vehicleId": 1,
          "licencePlate": "12345",
          "vehicleType": "suv",
          "brand": "benz",
          "manufactureYear": 2000,
          "userId": 1
        },
        "workOrderStatus": "已完成",
        "technicianDto": {
          "technicianId": 1,
          "username": "loading1",
          "name": "sy",
          "jobType": "漆工",
          "hourlyRate": 50.0,
          "contactInfo": "137",
          "income": 50.0
        },
        "problem": "掉漆",
        "submitTime": "2025-06-04T22:23:47",
        "progress": 1.0,
        "progressDescription": "做完了"
	      "laborCost": 50.0,
      "materialCost": 1.8,
      "totalCost": 51.8
      },
      "technician": {
        "technicianId": 1,
        "username": "loading1",
        "name": "sy",
        "jobType": "漆工",
        "hourlyRate": 50.0,
        "contactInfo": "137",
        "income": 50.0
      },
      "user": {
        "username": "loading1",
        "name": "sy",
        "id": 1,
        "contactInfo": "137",
        "registerTime": "2025-05-31T21:04:40"
      },
      "description": "做完了",
      "repairTime": "2025-06-04T22:24:06",
      "feedback": {
        "feedbackId": 1,
        "workOrderId": 1,
        "userId": 1,
        "rating": 5,
        "comment": "还可以",
        "feedbackTime": "2025-06-04T22:26:50"
      }
    }
  ]
}

#### 维修人员功能接口
1. **维修人员登录**
   - **路径**: `POST /api/technicians/login`
   - **请求参数**:
     ```json
     {
       "username": "string",
       "password": "string"
     }
     ```
   - **响应**:

{
  "code": 200,
  "message": "登录成功!",
  "data": null
}
   - **说明**: 验证维修人员身份

2. **查询维修人员信息**
   - **路径**: `GET /api/technicians/{technicianId}`
   - **请求参数**: 无（路径参数 `technicianId`）
   - **响应**:
     {
  "code": 200,
  "message": "查询成功",
  "data": {
    "technicianId": 1,
    "username": "loading1",
    "name": "sy",
    "jobType": "漆工",
    "hourlyRate": 50.0,
    "contactInfo": "137",
    "income": 0.0
  }
}
       ```
     - 404 Not Found: `{ "error": "维修人员不存在" }`
   - **说明**: 查询维修人员账户信息。

3. **接收/拒绝工单**
   - **路径**: PUT http://localhost:3000/api/technicians/{{technicianId}}/work-orders/{{notificationId}}/{{workOrderId}}/updateStatus
   - **请求参数**:
     ```json
     {
       "action": "accept" | "reject"
     }
     ```
   - **响应**:
     {
  "code": 200,
  "message": "状态更新成功",
  "data": {
    "workOrderId": 1,
    "vehicleResponse": {
      "vehicleId": 1,
      "licencePlate": "12345",
      "vehicleType": "suv",
      "brand": "bmw",
      "manufactureYear": 2000
    },
    "workOrderStatus": "进行中",
    "technicianDto": {
      "technicianId": 1,
      "username": "loading1",
      "name": "sy",
      "jobType": "漆工",
      "hourlyRate": 50.0,
      "contactInfo": "137",
      "income": 0.0
    },
    "problem": "车轮掉了",
    "submitTime": "2025-05-31T21:04:40",
    "progress": 0.0,
    "progressDescription": null
  }
}
   - **说明**: 维修人员根据notification接受或拒绝工单，拒绝后系统重新分配。

4. **更新维修进度**
   - **路径**:PUT http://localhost:3000/api/technicians/{technicianId}/work-orders/{workOrderId}/progress
   - **请求参数**:
    {
    "progress": 0.5 , // 大于等于1表示做完了
    "description": "做了一半"
}
   - **响应**:
 {
  "code": 200,
  "message": "进度更新成功",
  "data": {
    "workOrderId": 1,
    "vehicleResponse": {
      "vehicleId": 1,
      "licencePlate": "12345",
      "vehicleType": "suv",
      "brand": "benz",
      "manufactureYear": 2000,
      "userId": 1
    },
    "workOrderStatus": "进行中",
    "technicianDto": {
      "technicianId": 1,
      "username": "loading1",
      "name": "sy",
      "jobType": "漆工",
      "hourlyRate": 50.0,
      "contactInfo": "137",
      "income": 0.0
    },
    "problem": "掉漆",
    "submitTime": "2025-06-04T23:18:53",
    "progress": 0.5,
    "progressDescription": "做完了",
    "feedback": null
  }
}
   - **说明**: 维修人员更新工单进度并记录维修描述。

5. **记录材料消耗**
   - **路径**: POST http://localhost:3000/api/technicians/{{technicianId}}/work-orders/{{workOrderId}}/materials
   - **请求参数**:
     `{
  "materialId": 0,
  "workOrderId": 1,
  "materialName": "wood",
  "quantity": 2,
  "unitPrice": 0.9,
  "totalPrice": null
}
     ```
   - **响应**:
{
  "code": 201,
  "message": "材料记录成功",
  "data": {
    "materialId": 1,
    "workOrderId": 1,
    "materialName": "wood",
    "quantity": 2.0,
    "unitPrice": 0.9,
    "totalPrice": 1.8
  }
}
   - **说明**: 记录工单使用的材料，自动计算总价。

6. **查询历史维修记录**
   - **路径**: ###
GET http://localhost:3000/api/technicians/{{technicianId}}/repair-records
   - **请求参数**: 无（路径参数 `technicianId`）
Resposne  
 {
  "code": 200,
  "message": "查询成功",
  "data": []
}
有记录时
同用户查询历史记录返回结果

7.查询工单
GET http://localhost:3000/api/technicians/{{technicianId}}/work-orders
返回结果同用户查询工单，返回所有工单，包括已完成的工单

特殊接口:

###
GET http://localhost:3000/api/work-orders/{{workOrderId}}/feedback
查询某一条已完成工单的反馈
（前端在展示工单时，对已完成的工单可以有一个查看反馈按钮，在按下之后调用该接口获取反馈。当该工单没有反馈时，返回错误码为200，前端应当显示暂无反馈

{
  "code": 200,
  "message": "查询成功",
  "data": {
    "feedbackId": 1,
    "userId": 0,
    "rating": 5,
    "comment": "还可以",
    "feedbackTime": "2025-06-04T23:29:24"
  }
}


管理员接口

1. 查询所有用户信息
接口地址： /api/admin/users
GET /api/admin/users
✅ 返回示例：
{
  "code": 200,
  "message": "用户列表获取成功",
  "data": [
    {
      "username": "loading1",
      "name": "sy",
      "id": 1,
      "contactInfo": "137",
      "registerTime": "2025-05-31T21:04:40"
    },
    {
      "username": "loading2",
      "name": "sy",
      "id": 4,
      "contactInfo": "137",
      "registerTime": "2025-06-02T23:43:21"
    }
  ]
}
2. 查询所有维修人员信息
接口地址：/api /admin/technicians

请求方式： GET

功能说明： 查询所有维修人员的基本信息，包括工种与收入

GET /api/admin/technicians
Response：
{
  "code": 200,
  "message": "技师列表获取成功",
  "data": [
    {
      "technicianId": 1,
      "username": "loading",
      "name": "sy",
      "jobType": "漆工",
      "hourlyRate": 50.0,
      "contactInfo": "137",
      "income": 0.0
    },
    {
      "technicianId": 2,
      "username": "loading1",
      "name": "sy",
      "jobType": "漆工",
      "hourlyRate": 50.0,
      "contactInfo": "137",
      "income": 0.0
    }
  ]
}
3. 查询所有车辆信息
接口地址： /api/admin/vehicles

请求方式： GET

功能说明： 获取所有注册车辆的详细信息
✅ 示例请求：
GET /admin/vehicles
返回:
{
  "code": 200,
  "message": "车辆列表获取成功",
  "data": [
    {
      "vehicleId": 1,
      "licencePlate": "22345",
      "vehicleType": "suv",
      "brand": "benz",
      "manufactureYear": 2000,
      "userId": 1
    }
  ]
}
4. 查询所有维修工单信息
接口地址： /api/admin/work-orders

请求方式： GET

功能说明： 查询所有维修工单，包括进度、维修人员和车辆信息
GET /api/admin/work-orders
格式同用户返回工单

5. 查询所有历史维修记录与工时费发放
接口地址： /api/admin/repair-records

请求方式： GET

格式同用户返回维修记录