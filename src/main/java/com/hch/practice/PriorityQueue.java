package com.hch.practice;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import com.hch.practice.snake.PathCost;

import java.lang.Comparable;

// 根据java Priority简单实现的优先队列
public class PriorityQueue<T extends Comparable<T>> {
    

    int capacity; // 记录容量大小
    //int size = 0; // 实际存储多少元素
    // @SuppressWarnings("rawtypes")
    List<T> queue;

    PriorityQueue() {
        queue = new ArrayList<T>();
        // capacity = length;
    }

    public void add(T e) {
        // if ((size + 1) > capacity) throw new RuntimeException("超出容量");
        int k = queue.size();
        // 添加元素，首先将其放到数组末端，然后校验其父节点是否大于其，大于则调换位置，直到结构稳定
        while (k > 0) {
            int parent = (k - 1) >>> 1;
            T parentVal = queue.get(parent);
            if (e.compareTo(parentVal) >= 0) {
                break;
            }
            addEle(k, parentVal);
            k = parent;
        }
        addEle(k, e);// 默认将元素放到末端，如果经过调整k值会随之改变
        
        //size ++;    // 数量+1
        print();    // 打印队列元素
    }

    public void addEle(int k, T e) {
        if (k < queue.size()) {
            queue.set(k, e);   // 默认将元素放到末端，如果经过调整k值会随之改变
        } else {
            queue.add(e);
        }
    }

    // 弹出队首元素，即最小元素
    public T poll() {
        if ((queue.size() - 1) < 0) throw new RuntimeException("队列中没有元素了");
        T result = queue.get(0);
        T last = queue.get(queue.size() - 1);
        siftDown(0, last, queue.size());    // 将队尾元素放到队首，然后调整结构至稳定
        queue.remove(queue.size() - 1); // 去掉队尾元素
        print();
        // System.out.println(result);
        return result;
    }

    public void heapify() {
        // 判断堆结构是否正确
        int i = (queue.size() >>> 1) - 1;
        for (;i >= 0; i--) {
            siftDown(i, queue.get(i), queue.size());
        }
    }

    // 维护堆元素的结构
    public void siftDown(int k, T ele, int num) {
        int half = num >>> 1;
        while (k < half) {
            int child = (k << 1) + 1;
            int right = child + 1;
            int min = child;
            // 判断元素的左右子节点中最小值
            if (right < num && queue.get(right).compareTo(queue.get(child)) < 0) {
                min = right;
            }
            // 如果元素小于等于左右子节点的值，则结构稳定
            if (ele.compareTo(queue.get(min)) <= 0) {
                break;
            }
            queue.set(k, queue.get(min));
            k = min;
        }
        queue.set(k, ele); // 待调整元素的位置赋值
    }

    void print() {
        // StringBuilder builder = new StringBuilder();
        // builder.append("[");
        // for (int i = 0; i < queue.size(); i++) {
        //     builder.append(queue.get(i)).append(",");
        // }
        // if (builder.length() > 1) {
        //     builder.deleteCharAt(builder.length() - 1);
        // }
        // builder.append("]");
        // System.out.println(builder.toString());
    }

    public static void main(String[] args) {
        test2();
    }

    private static void test2() {
        PriorityQueue<PathCost> q = new PriorityQueue<>();
        q.add(new PathCost(10, 0, 1, 3));
        q.add(new PathCost(7, 0, 10, 3));
        q.add(new PathCost(13, 0, 2, 3));
        q.add(new PathCost(4, 0, 1, 4));
        q.add(new PathCost(8, 0, 5, 6));
        q.add(new PathCost(2, 0, 7, 7));

        q.poll();
        q.poll();
        q.poll();
        q.poll();
    }

    private static void test1() {

        PriorityQueue<Integer> q = new PriorityQueue<>();
        q.add(5);
        q.add(2);
        q.add(4);
        q.add(1);
        q.add(7);
        q.add(6);
        q.add(3);
        q.add(8);

        System.out.println("获取堆顶元素");
        //q.print(); // [ 1, 2, 3, 5, 7, 6, 4, 8 ]
        q.poll();
        q.poll();
        q.poll();
        //q.print(); // [ 8, 7, 6, 5, 4 ]
    }
}
