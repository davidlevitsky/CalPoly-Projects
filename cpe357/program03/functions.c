/* Garrett Milster
 *
 * this program contains the functions shared by both
 * hdecode and hencode.
 */
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "functions.h"
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <unistd.h>

/*opens files based on status, if status is 1, it opens for writing
 * and if status is 0 it only opens for reading */
int open_file(char *filename, int status)
{
   int fd;
   if(status == 1)
   {
      if((fd = open(filename, O_RDWR|O_CREAT|O_TRUNC,S_IRWXU)) < 0)
      {
         perror(filename);
         return -1;
      }
   }
   else
   {
      if((fd = open(filename, O_RDONLY)) < 0)
      {
         perror(filename);
         return -1;
      }
   }
   return fd;
}

/* build_list builds a linked list from the histogram table
 * made from read_file. */
Node* build_list(int char_list[],int int_list[], int index)
{
   Node *head;
   Node *previous;
   Node *ptr;
   int i;
   
   previous = safe_malloc(sizeof(Node));
   previous->next = NULL;
   previous->c = char_list[0];
   previous->count = int_list[0];
   head = previous;

   /* iterates through the array making nodes for each entry
    * until it gets to index. */
   for(i = 1; i < index; i++)
   {
      ptr = safe_malloc(sizeof(Node)); 
      ptr->next = NULL;
      ptr->c = char_list[i];
      ptr->count = int_list[i];
      previous->next = ptr;
      previous = ptr;

      
   }
   
   return head;
}

/* build_tree makes a binary tree according to the huffman model for encoding.
 * it takes the first two nodes, and sums their counts. it then takes this sum,
 * and makes a new node, who's count is the sum, and who's children are the two 
 * nodes. finally, the newly created node is re-inserted in the proper order, 
 * into the linked list. */ 
Node* build_tree(Node *head)
{
   Node *ptr = head;
   Node *next;
   Node *temp;
   Node *previous;
   int count = 0;

   while(ptr->next != NULL)
   {
      next = ptr->next;
      count = ptr->count + next->count;
      temp = safe_malloc(sizeof(Node));
      temp->count = count;
      temp->left = ptr;
      temp->right = next;
      temp->next = NULL;
      temp->c = -1;
      
      /* this checks to see if there are enough nodes left
       * to go through the loop again, if there are only two nodes
       * left in the list, then this will be the last run through */
      if(next->next == NULL)
      {
         head = temp;
         break;
      }
      else
      {
         head = next->next;
      }

      ptr->next = NULL;   
      next->next = NULL;

      ptr = head;
      previous = head;
      
      /* now that the new node has been created, this loop goes through
       * the linked list and inserts it in its proper place */
      while(ptr != NULL)
      {
         if(temp->count <= ptr->count)
         {
            if(ptr == head)
            {
               temp->next = head;
               head = temp;
               break;
            }
            previous->next = temp;
            temp->next = ptr;
            break;
         }
         previous = ptr;
         ptr = ptr->next;
      }
      if(ptr == NULL)
      {         
         previous->next = temp;
         temp->next = ptr;
      }
      ptr = head;
   }
   return head;
}


/* free_nodes simply recursively travels through the list
 * and frees all the allocated memory. */
void free_nodes(Node *root)
{
   if(root->left != NULL)
   {
      free_nodes(root->left);
   }
   if(root->right != NULL)
   {
      free_nodes(root->right);
   }
   
   free(root);
}

/* a traditional bubble sort algorithm with an extra case added.
 * if the counts of two characters are the same, it then checks
 * which character's integer value is higher. */
void bubbleSort(int int_list[], int char_list[], int index)
{
   
  int i, j, temp;

  for (i = index; i >= 0; i--)
  {     
    for (j = 1; j <= i; j++)
    {
      if (int_list[j-1] > int_list[j])
      {
        temp = int_list[j-1];
        int_list[j-1] = int_list[j];
        int_list[j] = temp;
        
        temp = char_list[j-1];
        char_list[j-1] = char_list[j];
        char_list[j] = temp;
      }
      else if(int_list[j-1] == int_list[j])
      {
         if(char_list[j-1] > char_list[j])
         {
            temp = int_list[j-1];
            int_list[j-1] = int_list[j];
            int_list[j] = temp;

            temp = char_list[j-1];
            char_list[j-1] = char_list[j];
            char_list[j] = temp;
         }
      }
    }
  }

}

/* safely mallocs data */
Node* safe_malloc(int size)
{
   Node* ptr = malloc(size);
   if(ptr == NULL)
   {
      perror("malloc");
      return NULL;
   }
   return ptr;
}
/* build_table makes a two dimensional array out of the binary tree.
 * it recursively visits each character in the tree and keeps track of the
 * path it took to get there, then records the path into the table at the
 * index related to the characters integer value. */
void build_table(Node *root, int index, int table[CHAR_LEN][CHAR_LEN])
{
   int i;
   
   /* if a character is enter the path into the table */
   if(root != NULL)
   {
      if(root->c != -1)
      {
         table[root->c][index] = -1;
         /* if there is only one character total in the file
          * this -2 assignment keeps it from being lost during 
          * the printing stage in main. */
         if(index == 0)
         {
            table[root->c][0] = -2;
         }

         for(i = 0; i < index; i++)
         {
            table[root->c][i] = table[CHAR_LEN - 1][i];
         }  
      }
   }
   
   i = index; /* this assignment keeps the function from losing 
               * the value of index when it enters left */
               
   /* this recursive code keeps track of the path taken and stores it 
    * in an extra row at the very bottom of the table. */
   if(root->left != NULL)
   {
      table[CHAR_LEN - 1][index] = 0;
      index ++;
      build_table(root->left, index, table);
   }
   if(root->right != NULL)
   {      
      table[CHAR_LEN - 1][i] = 1;
      i ++;
      build_table(root->right, i, table);
   }
}
