# ===== 构建阶段 =====
FROM node:18-alpine AS builder

WORKDIR /app

# 只复制依赖文件（利用缓存）
COPY package.json package-lock.json ./

# 使用国内源（关键优化）
RUN npm config set registry https://registry.npmmirror.com

# 安装依赖（使用 ci 更快更稳定）
RUN npm ci --legacy-peer-deps

# 再复制源码
COPY . .

# 构建
RUN npm run build

# ===== 运行阶段 =====
FROM nginx:alpine

COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=builder /app/dist /usr/share/nginx/html

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]